package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommentCommand
import jakarta.validation.constraints.NotBlank
import java.util.*

data class ModifyEbookInquiryCommentRequest(
    @field:NotBlank
    val content: String,
) {
    fun toCommand(commentId: UUID, requesterId: UUID) =
        ModifyEbookInquiryCommentCommand(
            content = content,
            commentId = commentId,
            requesterId = requesterId
        )
}
