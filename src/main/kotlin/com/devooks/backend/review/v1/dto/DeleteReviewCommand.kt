package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateReviewId
import java.util.*

data class DeleteReviewCommand(
    val reviewId: UUID,
    val requesterId: UUID,
) {
    constructor(
        reviewId: String,
        requesterId: UUID,
    ) : this(
        reviewId = reviewId.validateReviewId(),
        requesterId = requesterId
    )
}
