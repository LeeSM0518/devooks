package com.devooks.backend.auth.v1.domain

typealias AccessToken = String
typealias RefreshToken = String

data class TokenGroup(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
)