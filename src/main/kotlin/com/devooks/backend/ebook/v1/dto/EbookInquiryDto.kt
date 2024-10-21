package com.devooks.backend.ebook.v1.dto

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import java.time.Instant
import java.util.*

data class EbookInquiryDto(
    val id: UUID,
    val content: String,
    val ebookId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
) {
    companion object {
        fun EbookInquiry.toDto() =
            EbookInquiryDto(
                id = this.id,
                content = this.content,
                ebookId = this.ebookId,
                writerMemberId = this.writerMemberId,
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
