package com.devooks.backend.review.v1.dto

import java.time.Instant
import java.util.*

data class ReviewRow(
    val reviewId: UUID,
    val content: String,
    val rating: Int,
    val ebookId: UUID,
    val memberId: UUID,
    val nickname: String,
    val profileImagePath: String?,
    val writtenDate: Instant,
    val modifiedDate: Instant,
    val commentCount: Int,
)
