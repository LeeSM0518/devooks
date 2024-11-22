package com.devooks.backend.review.v1.dto

import io.swagger.v3.oas.annotations.media.Schema

data class DeleteReviewResponse(
    @Schema(description = "결과 메시지", example = "리뷰 삭제를 완료했습니다.")
    val message: String = "리뷰 삭제를 완료했습니다."
)
