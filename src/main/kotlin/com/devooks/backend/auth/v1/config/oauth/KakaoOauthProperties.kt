package com.devooks.backend.auth.v1.config.oauth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kakao")
data class KakaoOauthProperties(
    val clientId: String,
    val redirectUri: String
)