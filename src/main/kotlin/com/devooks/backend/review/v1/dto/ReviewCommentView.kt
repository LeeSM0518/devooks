package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.review.v1.domain.ReviewComment
import com.devooks.backend.review.v1.dto.WriterView.Companion.toWriterView
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
    @Schema(description = "작성자")
    val writer: WriterView,
    @Schema(description = "작성 날짜")
    val writtenDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
) {
    companion object {
        fun ReviewCommentRow.toReviewCommentView(): ReviewCommentView =
            ReviewCommentView(
                id = this.reviewCommentId,
                content = this.content,
                reviewId = this.reviewId,
                writer = this.toWriterView(),
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )

        fun ReviewComment.toReviewCommentView(member: Member): ReviewCommentView =
            ReviewCommentView(
                id = this.id,
                content = this.content,
                reviewId = this.reviewId,
                writer = member.toWriterView(),
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
