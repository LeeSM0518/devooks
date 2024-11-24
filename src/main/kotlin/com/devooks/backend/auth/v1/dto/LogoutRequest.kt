package com.devooks.backend.auth.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class LogoutRequest(
    @field:NotBlank
    @Schema(description = "Refresh 토큰", required = true)
    val refreshToken: String,
) {
    fun toCommand(): LogoutCommand =
        LogoutCommand(refreshToken = refreshToken)
}
