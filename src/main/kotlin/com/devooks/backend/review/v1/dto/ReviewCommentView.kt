package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.ReviewComment
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class ReviewCommentView(
    @Schema(description = "리뷰 댓글 식별자")
    val id: UUID,
    @Schema(description = "내용")
    val content: String,
    @Schema(description = "리뷰 식별자")
    val reviewId: UUID,
    @Schema(description = "작성자 식별자")
    val writerMemberId: UUID,
    @Schema(description = "작성 날짜")
    val writtenDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
) {
    companion object {
        fun ReviewComment.toReviewCommentView(): ReviewCommentView =
            ReviewCommentView(
                id = this.id,
                content = this.content,
                reviewId = this.reviewId,
                writerMemberId = this.writerMemberId,
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
