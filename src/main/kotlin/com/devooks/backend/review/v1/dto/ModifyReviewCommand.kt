package com.devooks.backend.review.v1.dto

import java.util.*

class ModifyReviewCommand(
    val reviewId: UUID,
    val rating: Int?,
    val content: String?,
    val requesterId: UUID,
)
