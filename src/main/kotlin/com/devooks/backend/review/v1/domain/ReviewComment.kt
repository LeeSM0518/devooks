package com.devooks.backend.review.v1.domain

import java.time.Instant
import java.util.*

class ReviewComment(
    val id: UUID,
    val content: String,
    val reviewId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
)
