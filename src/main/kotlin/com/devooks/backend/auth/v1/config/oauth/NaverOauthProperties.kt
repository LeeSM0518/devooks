package com.devooks.backend.auth.v1.config.oauth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "naver")
data class NaverOauthProperties(
    val clientSecret: String,
    val clientId: String,
    val state: String,
)