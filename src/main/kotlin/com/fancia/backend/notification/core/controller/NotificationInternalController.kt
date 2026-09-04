package com.fancia.backend.notification.core.controller

import com.fancia.backend.notification.core.service.EmailNotificationService
import com.fancia.backend.notification.core.service.PushNotificationService
import com.fancia.backend.shared.notification.core.dto.SendPasswordResetEmailRequest
import com.fancia.backend.shared.notification.core.dto.SendPushNotificationRequest
import com.fancia.backend.shared.notification.core.dto.SendWelcomeEmailRequest
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
@Hidden
@Tag(name = "Notifications Internal", description = "Internal notification endpoints for service-to-service calls")
class NotificationInternalController(
    private val pushNotificationService: PushNotificationService,
    private val emailNotificationService: EmailNotificationService,
) {
    @Operation(summary = "Send push notification")
    @PostMapping("/push")
    fun sendPush(
        @RequestBody @Valid request: SendPushNotificationRequest,
    ): ResponseEntity<Void> {
        pushNotificationService.send(request)
        return ResponseEntity.accepted().build()
    }

    @Operation(summary = "Send welcome / verify-email message")
    @PostMapping("/emails/welcome")
    fun sendWelcomeEmail(
        @RequestBody @Valid request: SendWelcomeEmailRequest,
    ): ResponseEntity<Void> {
        emailNotificationService.sendWelcome(request)
        return ResponseEntity.accepted().build()
    }

    @Operation(summary = "Send password-reset email")
    @PostMapping("/emails/password-reset")
    fun sendPasswordResetEmail(
        @RequestBody @Valid request: SendPasswordResetEmailRequest,
    ): ResponseEntity<Void> {
        emailNotificationService.sendPasswordReset(request)
        return ResponseEntity.accepted().build()
    }
}
