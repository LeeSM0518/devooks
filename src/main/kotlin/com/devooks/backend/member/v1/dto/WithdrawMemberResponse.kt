package com.devooks.backend.member.v1.dto

import io.swagger.v3.oas.annotations.media.Schema

data class WithdrawMemberResponse(
    @Schema(description = "결과 메시지", example = "탈퇴가 완료됐습니다.")
    val message: String = "탈퇴가 완료됐습니다.",
)
