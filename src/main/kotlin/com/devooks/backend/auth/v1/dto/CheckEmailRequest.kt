package com.devooks.backend.auth.v1.dto

import com.devooks.backend.member.v1.error.validateEmail
import io.swagger.v3.oas.annotations.media.Schema

data class CheckEmailRequest(
    @Schema(description = "이메일", required = true, nullable = false)
    val email: String?,
) {
    fun toCommand() =
        CheckEmailCommand(
            email = email.validateEmail()
        )
}
