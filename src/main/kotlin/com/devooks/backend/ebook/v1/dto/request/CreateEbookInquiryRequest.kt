package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommand
import com.devooks.backend.ebook.v1.error.validateEbookInquiryContent
import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*

data class CreateEbookInquiryRequest(
    val ebookId: String?,
    val content: String?,
) {
    fun toCommand(requesterId: UUID) =
        CreateEbookInquiryCommand(
            ebookId = ebookId.validateEbookId(),
            content = content.validateEbookInquiryContent(),
            requesterId = requesterId
        )
}
