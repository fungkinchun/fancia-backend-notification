package com.fancia.backend.notification.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class ApplicationProperties {
    var allowedOrigins: List<String> = emptyList()
    var applicationName: String? = null
    var baseUrl: String? = null
    var loginPageUrl: String? = null
}
