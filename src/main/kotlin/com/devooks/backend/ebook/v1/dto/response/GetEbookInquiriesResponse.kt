package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import com.devooks.backend.ebook.v1.dto.EbookInquiryDto
import com.devooks.backend.ebook.v1.dto.EbookInquiryDto.Companion.toDto

data class GetEbookInquiriesResponse(
    val ebookInquiryList: List<EbookInquiryDto>,
) {
    companion object {
        fun List<EbookInquiry>.toGetEbookInquiriesResponse() =
            GetEbookInquiriesResponse(map { it.toDto() })
    }
}
