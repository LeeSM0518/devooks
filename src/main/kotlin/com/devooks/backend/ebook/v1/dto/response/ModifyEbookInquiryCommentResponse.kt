package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentDto
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentDto.Companion.toDto

data class ModifyEbookInquiryCommentResponse(
    val comment: EbookInquiryCommentDto,
) {
    companion object {
        fun EbookInquiryComment.toModifyEbookInquiryCommentResponse() =
            ModifyEbookInquiryCommentResponse(toDto())
    }
}


