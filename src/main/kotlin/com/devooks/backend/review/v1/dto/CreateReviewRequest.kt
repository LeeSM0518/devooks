package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateRating
import com.devooks.backend.review.v1.error.validateReviewContent
import com.devooks.backend.wishlist.v1.error.validateEbookId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateReviewRequest(
    @Schema(description = "전자책 식별자", required = true)
    val ebookId: String?,
    @Schema(description = "평점 (0~5점)", required = true)
    val rating: String?,
    @Schema(description = "내용", required = true)
    val content: String?
) {
    fun toCommand(requesterId: UUID): CreateReviewCommand =
        CreateReviewCommand(
            ebookId = ebookId.validateEbookId(),
            rating = rating.validateRating(),
            content = content.validateReviewContent(),
            requesterId = requesterId
        )
}
