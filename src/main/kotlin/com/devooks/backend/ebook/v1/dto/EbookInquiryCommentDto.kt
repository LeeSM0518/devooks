package com.devooks.backend.ebook.v1.dto

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import java.time.Instant
import java.util.*

data class EbookInquiryCommentDto(
    val id: UUID,
    val content: String,
    val inquiryId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
) {
    companion object {
        fun EbookInquiryComment.toDto() =
            EbookInquiryCommentDto(
                id = this.id,
                content = this.content,
                inquiryId = this.inquiryId,
                writerMemberId = this.writerMemberId,
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
