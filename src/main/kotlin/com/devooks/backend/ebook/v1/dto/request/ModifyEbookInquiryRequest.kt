package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommand
import com.devooks.backend.ebook.v1.error.validateEbookInquiryContent
import com.devooks.backend.ebook.v1.error.validateEbookInquiryId
import java.util.*

data class ModifyEbookInquiryRequest(
    val content: String?,
) {
    fun toCommand(inquiryId: String, requesterId: UUID) =
        ModifyEbookInquiryCommand(
            content = content.validateEbookInquiryContent(),
            inquiryId = inquiryId.validateEbookInquiryId(),
            requesterId = requesterId,
        )
}
