package com.devooks.backend.ebook.v1.domain

import java.time.Instant
import java.util.*

class EbookInquiry(
    val id: UUID,
    val content: String,
    val ebookId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
)
