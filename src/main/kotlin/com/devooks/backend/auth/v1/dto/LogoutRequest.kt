package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.error.validateRefreshToken
import io.swagger.v3.oas.annotations.media.Schema

data class LogoutRequest(
    @Schema(description = "Refresh 토큰", required = true, nullable = false)
    val refreshToken: String?,
) {
    fun toCommand(): LogoutCommand =
        LogoutCommand(
            refreshToken = refreshToken.validateRefreshToken()
        )
}
