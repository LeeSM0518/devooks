package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentView
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentView.Companion.toEbookInquiryCommentView

data class GetEbookInquiryCommentsResponse(
    val comments: List<EbookInquiryCommentView>,
) {
    companion object {
        fun List<EbookInquiryComment>.toGetEbookInquiryCommentsResponse() =
            GetEbookInquiryCommentsResponse(map { it.toEbookInquiryCommentView() })
    }
}
