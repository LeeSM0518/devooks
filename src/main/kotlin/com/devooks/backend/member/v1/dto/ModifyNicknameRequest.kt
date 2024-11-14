package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.error.validateNickname
import io.swagger.v3.oas.annotations.media.Schema

data class ModifyNicknameRequest(
    @Schema(description = "닉네임", required = true, nullable = false)
    val nickname: String?
) {
    fun toCommand(): ModifyNicknameCommand =
        ModifyNicknameCommand(
            nickname = nickname.validateNickname()
        )
}
