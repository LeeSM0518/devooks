package com.devooks.backend.auth.v1.dto

import com.devooks.backend.member.v1.error.validateEmail

data class CheckEmailRequest(
    val email: String?
) {
    fun toCommand() =
        CheckEmailCommand(
            email = email.validateEmail()
        )
}
