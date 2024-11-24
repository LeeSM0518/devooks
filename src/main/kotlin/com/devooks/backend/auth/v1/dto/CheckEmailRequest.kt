package com.devooks.backend.auth.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern

data class CheckEmailRequest(
    @field:Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+\\.[A-Za-z]+$")
    @Schema(description = "이메일", required = true)
    val email: String,
) {
    fun toCommand() = CheckEmailCommand(email = email)
}
