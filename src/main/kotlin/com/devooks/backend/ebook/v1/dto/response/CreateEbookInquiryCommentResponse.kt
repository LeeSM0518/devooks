package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentView
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentView.Companion.toEbookInquiryCommentView

data class CreateEbookInquiryCommentResponse(
    val comment: EbookInquiryCommentView,
) {
    companion object {
        fun EbookInquiryComment.toCreateEbookInquiryCommentResponse() =
            CreateEbookInquiryCommentResponse(toEbookInquiryCommentView())
    }
}
