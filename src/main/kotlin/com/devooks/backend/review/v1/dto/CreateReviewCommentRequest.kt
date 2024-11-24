package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateReviewContent
import com.devooks.backend.review.v1.error.validateReviewId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateReviewCommentRequest(
    @Schema(description = "리뷰 식별자", required = true)
    val reviewId: String?,
    @Schema(description = "내용", required = true)
    val content: String?,
) {
    fun toCommand(requesterId: UUID): CreateReviewCommentCommand =
        CreateReviewCommentCommand(
            reviewId = reviewId.validateReviewId(),
            content = content.validateReviewContent(),
            requesterId = requesterId,
        )
}
