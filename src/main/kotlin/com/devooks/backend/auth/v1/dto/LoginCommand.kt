package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.domain.OauthType

data class LoginCommand(
    val authorizationCode: String,
    val oauthType: OauthType,
)
