package com.devooks.backend.common.exception

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

class ErrorResponse(
    @Schema(description = "에러 발생 날짜", example = "2024-11-04T05:05:43.264+00:00")
    val timestamp: Date,
    @Schema(description = "에러 발생 API 경로", example = "/api/v1/ebook-inquiry-comments")
    val path: String,
    @Schema(description = "에러 상태 코드", example = "400")
    val status: Int,
    @Schema(description = "에러 유형", example = "BAD_REQUEST")
    val error: String,
    @Schema(description = "요청 식별자", example = "ebad5bcb-5")
    val requestId: String,
    @Schema(description = "에러 코드", example = "EBOOK-400-11")
    val code: String,
    @Schema(description = "에러 메시지", example = "문의 식별자가 반드시 필요합니다.")
    val message: String,
)
