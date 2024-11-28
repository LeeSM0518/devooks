package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import com.devooks.backend.ebook.v1.dto.EbookInquiryView
import com.devooks.backend.ebook.v1.dto.EbookInquiryView.Companion.toEbookInquiryView

data class CreateEbookInquiryResponse(
    val ebookInquiry: EbookInquiryView,
) {
    companion object {
        fun EbookInquiry.toCreateEbookInquiryResponse() =
            CreateEbookInquiryResponse(toEbookInquiryView())
    }
}
