package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateReviewContent
import com.devooks.backend.review.v1.error.validateReviewId
import java.util.*

data class CreateReviewCommentRequest(
    val reviewId: String?,
    val content: String?,
) {
    fun toCommand(requesterId: UUID): CreateReviewCommentCommand =
        CreateReviewCommentCommand(
            reviewId = reviewId.validateReviewId(),
            content = content.validateReviewContent(),
            requesterId = requesterId,
        )
}
