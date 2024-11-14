package com.devooks.backend.auth.v1.domain

import io.swagger.v3.oas.annotations.media.Schema

typealias AccessToken = String
typealias RefreshToken = String

data class TokenGroup(
    @Schema(description = "액세스 토큰")
    val accessToken: AccessToken,
    @Schema(description = "리프래시 토큰")
    val refreshToken: RefreshToken,
)
