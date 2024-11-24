package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommentCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.util.*

data class CreateEbookInquiryCommentRequest(
    @Schema(description = "전자책 문의 식별자", required = true, implementation = UUID::class)
    val inquiryId: UUID,
    @field:NotBlank
    @Schema(description = "내용", required = true)
    val content: String,
) {
    fun toCommand(requesterId: UUID) =
        CreateEbookInquiryCommentCommand(
            inquiryId = inquiryId,
            content = content,
            requesterId = requesterId
        )

}
