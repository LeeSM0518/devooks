package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommand
import com.devooks.backend.ebook.v1.error.validateEbookInquiryContent
import com.devooks.backend.wishlist.v1.error.validateEbookId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateEbookInquiryRequest(
    @Schema(description = "전자책 식별자", required = true)
    val ebookId: String?,
    @Schema(description = "내용", required = true)
    val content: String?,
) {
    fun toCommand(requesterId: UUID) =
        CreateEbookInquiryCommand(
            ebookId = ebookId.validateEbookId(),
            content = content.validateEbookInquiryContent(),
            requesterId = requesterId
        )
}
