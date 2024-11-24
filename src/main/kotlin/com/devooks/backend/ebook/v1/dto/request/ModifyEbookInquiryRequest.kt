package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommand
import com.devooks.backend.ebook.v1.error.validateEbookInquiryContent
import com.devooks.backend.ebook.v1.error.validateEbookInquiryId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ModifyEbookInquiryRequest(
    @Schema(description = "내용", required = true)
    val content: String?,
) {
    fun toCommand(inquiryId: String, requesterId: UUID) =
        ModifyEbookInquiryCommand(
            content = content.validateEbookInquiryContent(),
            inquiryId = inquiryId.validateEbookInquiryId(),
            requesterId = requesterId,
        )
}
