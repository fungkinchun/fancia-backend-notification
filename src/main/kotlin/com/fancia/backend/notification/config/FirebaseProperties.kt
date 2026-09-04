package com.fancia.backend.notification.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.firebase")
class FirebaseProperties {
    var enabled: Boolean = false
    var credentialsJson: String? = null
}
