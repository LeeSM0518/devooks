package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.util.*

data class CreateEbookInquiryRequest(
    @Schema(description = "전자책 식별자", implementation = UUID::class, required = true)
    val ebookId: UUID,
    @field:NotBlank
    @Schema(description = "내용", required = true)
    val content: String,
) {
    fun toCommand(requesterId: UUID) =
        CreateEbookInquiryCommand(
            ebookId = ebookId,
            content = content,
            requesterId = requesterId
        )
}
