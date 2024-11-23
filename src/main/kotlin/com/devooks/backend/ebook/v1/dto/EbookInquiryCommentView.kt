package com.devooks.backend.ebook.v1.dto

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class EbookInquiryCommentView(
    @Schema(description = "전자책 문의 댓글 식별자")
    val id: UUID,
    @Schema(description = "내용")
    val content: String,
    @Schema(description = "전자책 문의 식별자")
    val inquiryId: UUID,
    @Schema(description = "작성자 식별자")
    val writerMemberId: UUID,
    @Schema(description = "작성 날짜")
    val writtenDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
) {
    companion object {
        fun EbookInquiryComment.toEbookInquiryCommentView() =
            EbookInquiryCommentView(
                id = this.id,
                content = this.content,
                inquiryId = this.inquiryId,
                writerMemberId = this.writerMemberId,
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
