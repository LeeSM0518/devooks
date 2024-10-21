package com.devooks.backend.auth.v1.dto

data class LogoutCommand(
    val refreshToken: String,
)
