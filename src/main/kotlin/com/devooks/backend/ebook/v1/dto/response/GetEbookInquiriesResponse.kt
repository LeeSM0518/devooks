package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import com.devooks.backend.ebook.v1.dto.EbookInquiryView
import com.devooks.backend.ebook.v1.dto.EbookInquiryView.Companion.toEbookInquiryView

data class GetEbookInquiriesResponse(
    val ebookInquiryList: List<EbookInquiryView>,
) {
    companion object {
        fun List<EbookInquiry>.toGetEbookInquiriesResponse() =
            GetEbookInquiriesResponse(map { it.toEbookInquiryView() })
    }
}
