package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class ModifyEbookInquiryCommentCommand(
    val content: String,
    val commentId: UUID,
    val requesterId: UUID,
)
