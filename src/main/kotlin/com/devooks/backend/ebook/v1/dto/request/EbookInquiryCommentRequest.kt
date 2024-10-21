package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.error.validateEbookInquiryCommentContent
import com.devooks.backend.ebook.v1.error.validateEbookInquiryId
import java.util.*

data class CreateEbookInquiryCommentRequest(
    val inquiryId: String?,
    val content: String?,
) {
    fun toCommand(requesterId: UUID) =
        CreateEbookInquiryCommentCommand(
            inquiryId = inquiryId.validateEbookInquiryId(),
            content = content.validateEbookInquiryCommentContent(),
            requesterId = requesterId
        )

}