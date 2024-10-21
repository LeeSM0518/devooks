package com.devooks.backend.ebook.v1.domain

import java.time.Instant
import java.util.*

class EbookInquiryComment(
    val id: UUID,
    val inquiryId: UUID,
    val content: String,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
)
