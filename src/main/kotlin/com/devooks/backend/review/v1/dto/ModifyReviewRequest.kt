package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateRating
import com.devooks.backend.review.v1.error.validateReviewContent
import com.devooks.backend.review.v1.error.validateReviewId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ModifyReviewRequest(
    @Schema(description = "평점 (0~5점)", required = true, nullable = false)
    val rating: String?,
    @Schema(description = "내용", required = true, nullable = false)
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
