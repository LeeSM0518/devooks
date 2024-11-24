package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommand
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ModifyEbookInquiryRequest(
    @Schema(description = "내용", required = true)
    val content: String,
) {
    fun toCommand(inquiryId: UUID, requesterId: UUID) =
        ModifyEbookInquiryCommand(
            content = content,
            inquiryId = inquiryId,
            requesterId = requesterId,
        )
}
