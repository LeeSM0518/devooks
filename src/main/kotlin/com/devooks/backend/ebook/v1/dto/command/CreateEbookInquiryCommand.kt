package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class CreateEbookInquiryCommand(
    val ebookId: UUID,
    val content: String,
    val requesterId: UUID
)
