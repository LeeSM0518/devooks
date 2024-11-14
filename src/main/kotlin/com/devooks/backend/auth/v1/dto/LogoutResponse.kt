package com.devooks.backend.auth.v1.dto

import io.swagger.v3.oas.annotations.media.Schema

data class LogoutResponse(
    @Schema(description = "결과 메시지", example = "로그아웃이 완료됐습니다.")
    val message: String = "로그아웃이 완료됐습니다."
)
