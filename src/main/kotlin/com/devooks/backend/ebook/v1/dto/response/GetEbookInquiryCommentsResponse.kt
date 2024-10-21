package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentDto
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentDto.Companion.toDto

data class GetEbookInquiryCommentsResponse(
    val comments: List<EbookInquiryCommentDto>,
) {
    companion object {
        fun List<EbookInquiryComment>.toGetEbookInquiryCommentsResponse() =
            GetEbookInquiryCommentsResponse(map { it.toDto() })
    }
}