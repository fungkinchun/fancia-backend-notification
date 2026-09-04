package com.fancia.backend.notification.core.service

import com.fancia.backend.shared.notification.core.dto.SendPasswordResetEmailRequest
import com.fancia.backend.shared.notification.core.dto.SendWelcomeEmailRequest
import com.fancia.backend.notification.email.EmailService
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailNotificationService(
    private val templateEngine: SpringTemplateEngine,
    private val emailService: EmailService,
) {
    fun sendWelcome(request: SendWelcomeEmailRequest) {
        val thymeleafContext = Context().apply {
            setVariable("firstName", request.firstName)
            setVariable("verificationLink", request.verificationLink)
        }
        val htmlBody = templateEngine.process("welcome-email", thymeleafContext)
        emailService.sendHtmlMessage(
            listOf(request.to),
            "Welcome to Fancia — verify your email",
            htmlBody,
        )
    }

    fun sendPasswordReset(request: SendPasswordResetEmailRequest) {
        val firstName = request.firstName?.takeIf { it.isNotBlank() } ?: "there"
        val thymeleafContext = Context().apply {
            setVariable("firstName", firstName)
            setVariable("resetLink", request.resetLink)
        }
        val htmlBody = templateEngine.process("password-reset", thymeleafContext)
        emailService.sendHtmlMessage(
            listOf(request.to),
            "Reset your Fancia password",
            htmlBody,
        )
    }
}
