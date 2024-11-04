package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.error.validateRefreshToken
import io.swagger.v3.oas.annotations.media.Schema

data class ReissueRequest(
    @Schema(description = "리프래시 토큰", required = true, nullable = false)
    val refreshToken: String?,
) {
    fun toCommand(): ReissueCommand =
        ReissueCommand(
            refreshToken = refreshToken.validateRefreshToken()
        )
}
