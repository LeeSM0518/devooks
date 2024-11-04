package com.devooks.backend.auth.v1.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CheckEmailResponse(
    @Schema(description = "결과 메시지", example = "이메일 인증이 완료됐습니다.")
    val message: String = "이메일 인증이 완료됐습니다."
)
