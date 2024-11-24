package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateReviewContent
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateReviewCommentRequest(
    @Schema(description = "리뷰 식별자", required = true, implementation = UUID::class)
    val reviewId: UUID,
    @Schema(description = "내용", required = true)
    val content: String?,
) {
    fun toCommand(requesterId: UUID): CreateReviewCommentCommand =
        CreateReviewCommentCommand(
            reviewId = reviewId,
            content = content.validateReviewContent(),
            requesterId = requesterId,
        )
}
