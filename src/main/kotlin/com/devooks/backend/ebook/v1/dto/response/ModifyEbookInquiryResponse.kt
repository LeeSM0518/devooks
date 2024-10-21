package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import com.devooks.backend.ebook.v1.dto.EbookInquiryDto
import com.devooks.backend.ebook.v1.dto.EbookInquiryDto.Companion.toDto

data class ModifyEbookInquiryResponse(
    val ebookInquiry: EbookInquiryDto
) {
    companion object {
        fun EbookInquiry.toModifyEbookInquiryResponse() =
            ModifyEbookInquiryResponse(toDto())
    }
}
