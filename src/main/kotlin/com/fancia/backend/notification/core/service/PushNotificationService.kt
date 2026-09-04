package com.fancia.backend.notification.core.service

import com.fancia.backend.shared.notification.core.dto.SendPushNotificationRequest
import com.fancia.backend.shared.user.core.dto.UserNotificationSettings
import com.fancia.backend.shared.user.core.enums.NotificationChannel
import com.fancia.backend.shared.user.core.enums.allowsPush
import com.fancia.backend.notification.external.UserInternalClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PushNotificationService(
    private val userInternalClient: UserInternalClient,
    @Autowired(required = false)
    private val firebaseMessaging: FirebaseMessaging?,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun send(request: SendPushNotificationRequest) {
        val user = try {
            userInternalClient.getUser(request.userId)
        } catch (ex: Exception) {
            log.warn("Skipping FCM for user {}: failed to load user", request.userId, ex)
            return
        }
        val notifications = user.notifications
        val preferenceChannel = channelFor(notifications, request.preference)
        if (!preferenceChannel.allowsPush()) {
            log.debug(
                "Skipping FCM for user {}: channel does not allow push ({})",
                request.userId,
                preferenceChannel,
            )
            return
        }
        val token = notifications.fcmToken
        if (token.isNullOrBlank()) {
            log.debug("Skipping FCM for user {}: no fcm token", request.userId)
            return
        }
        val messaging = firebaseMessaging
        if (messaging == null) {
            log.debug("Skipping FCM for user {}: Firebase is disabled", request.userId)
            return
        }

        try {
            val builder = Message.builder()
                .setToken(token)
                .setNotification(
                    Notification.builder()
                        .setTitle(request.title)
                        .setBody(request.body)
                        .build(),
                )
                .putData("type", request.type)
                .putData("userId", request.userId.toString())
                .putData("title", request.title)
                .putData("body", request.body)
            if (!request.path.isNullOrBlank()) {
                builder.putData("path", request.path)
            }
            request.data.forEach { (key, value) -> builder.putData(key, value) }
            messaging.send(builder.build())
            log.info("Sent {} FCM notification to user {}", request.type, request.userId)
        } catch (ex: Exception) {
            log.warn("Failed to send {} FCM notification to user {}", request.type, request.userId, ex)
        }
    }

    companion object {
        fun channelFor(settings: UserNotificationSettings?, preferenceKey: String): NotificationChannel? =
            when (preferenceKey) {
                "match" -> settings?.match
                "messages" -> settings?.messages
                "postEngagement" -> settings?.postEngagement
                "eventRecommendations" -> settings?.eventRecommendations
                "eventReminders" -> settings?.eventReminders
                else -> settings?.eventReminders
            }
    }
}
