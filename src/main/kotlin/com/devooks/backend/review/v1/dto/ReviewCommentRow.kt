package com.devooks.backend.review.v1.dto

import java.time.Instant
import java.util.*

data class ReviewCommentRow(
    val reviewCommentId: UUID,
    val content: String,
    val reviewId: UUID,
    val memberId: UUID,
    val nickname: String,
    val profileImagePath: String?,
    val writtenDate: Instant,
    val modifiedDate: Instant,
)
