package com.devooks.backend.auth.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class ReissueRequest(
    @field:NotBlank
    @Schema(description = "리프래시 토큰", required = true)
    val refreshToken: String,
) {
    fun toCommand(): ReissueCommand = ReissueCommand(refreshToken = refreshToken)
}
