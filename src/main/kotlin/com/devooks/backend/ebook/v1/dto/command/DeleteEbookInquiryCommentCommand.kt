package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.ebook.v1.error.validateEbookInquiryCommentId
import java.util.*

class DeleteEbookInquiryCommentCommand(
    val commentId: UUID,
    val requesterId: UUID,
) {
    constructor(
        commentId: String,
        requesterId: UUID,
    ) : this(
        commentId = commentId.validateEbookInquiryCommentId(),
        requesterId = requesterId
    )
}
