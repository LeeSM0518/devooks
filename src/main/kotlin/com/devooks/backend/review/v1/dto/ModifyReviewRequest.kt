package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateRating
import com.devooks.backend.review.v1.error.validateReviewContent
import com.devooks.backend.review.v1.error.validateReviewId
import java.util.*

data class ModifyReviewRequest(
    val rating: String?,
    val content: String?,
) {
    fun toCommand(reviewId: String, requesterId: UUID): ModifyReviewCommand =
        ModifyReviewCommand(
            reviewId = reviewId.validateReviewId(),
            rating = rating.validateRating(),
            content = content.validateReviewContent(),
            requesterId = requesterId
        )
}
