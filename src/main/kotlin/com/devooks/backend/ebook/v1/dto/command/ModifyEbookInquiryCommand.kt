package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class ModifyEbookInquiryCommand(
    val content: String,
    val inquiryId: UUID,
    val requesterId: UUID,
)
