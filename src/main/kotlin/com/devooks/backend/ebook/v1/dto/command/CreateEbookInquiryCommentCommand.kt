package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class CreateEbookInquiryCommentCommand(
    val inquiryId: UUID,
    val content: String,
    val requesterId: UUID,
)
