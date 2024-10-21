package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.error.validateNickname

data class ModifyNicknameRequest(
    val nickname: String?
) {
    fun toCommand(): ModifyNicknameCommand =
        ModifyNicknameCommand(
            nickname = nickname.validateNickname()
        )
}
