package com.devooks.backend.auth.v1.config.oauth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google")
data class GoogleOauthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String
)