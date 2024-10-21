package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.error.validateEbookInquiryCommentContent
import com.devooks.backend.ebook.v1.error.validateEbookInquiryCommentId
import java.util.*

data class ModifyEbookInquiryCommentRequest(
    val content: String?,
) {
    fun toCommand(commentId: String, requesterId: UUID) =
        ModifyEbookInquiryCommentCommand(
            content = content.validateEbookInquiryCommentContent(),
            commentId = commentId.validateEbookInquiryCommentId(),
            requesterId = requesterId
        )
}
