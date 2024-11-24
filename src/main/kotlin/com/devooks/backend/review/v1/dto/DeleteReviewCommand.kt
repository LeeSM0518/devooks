package com.devooks.backend.review.v1.dto

import java.util.*

data class DeleteReviewCommand(
    val reviewId: UUID,
    val requesterId: UUID,
)
