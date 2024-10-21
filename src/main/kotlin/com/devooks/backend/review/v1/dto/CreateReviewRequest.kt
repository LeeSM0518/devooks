package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateRating
import com.devooks.backend.review.v1.error.validateReviewContent
import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*

data class CreateReviewRequest(
    val ebookId: String?,
    val rating: String?,
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
