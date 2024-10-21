package com.devooks.backend.review.v1.dto

import java.util.*

class CreateReviewCommentCommand(
    val reviewId: UUID,
    val content: String,
    val requesterId: UUID,
)
