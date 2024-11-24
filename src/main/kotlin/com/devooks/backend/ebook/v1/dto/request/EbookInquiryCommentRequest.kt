package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.error.validateEbookInquiryCommentContent
import com.devooks.backend.ebook.v1.error.validateEbookInquiryId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateEbookInquiryCommentRequest(
    @Schema(description = "전자책 문의 식별자", required = true)
    val inquiryId: String?,
    @Schema(description = "내용", required = true)
    val content: String?,
) {
    fun toCommand(requesterId: UUID) =
        CreateEbookInquiryCommentCommand(
            inquiryId = inquiryId.validateEbookInquiryId(),
            content = content.validateEbookInquiryCommentContent(),
            requesterId = requesterId
        )

}
