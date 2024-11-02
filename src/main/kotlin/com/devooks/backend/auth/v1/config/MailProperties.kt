package com.devooks.backend.auth.v1.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.mail")
data class MailProperties(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val protocol: String,
    val timeout: Int,
    val auth: Boolean,
    val tls: Boolean,
    val debug: Boolean,
)
