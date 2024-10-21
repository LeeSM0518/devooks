package com.devooks.backend.review.v1.domain

import java.time.Instant
import java.util.*

class Review(
    val id: UUID,
    val rating: Int,
    val content: String,
    val ebookId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
)
