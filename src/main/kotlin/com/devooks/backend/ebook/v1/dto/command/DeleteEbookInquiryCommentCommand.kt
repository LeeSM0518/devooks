package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class DeleteEbookInquiryCommentCommand(
    val commentId: UUID,
    val requesterId: UUID,
)
