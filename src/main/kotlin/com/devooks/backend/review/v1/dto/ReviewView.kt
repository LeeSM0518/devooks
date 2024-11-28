package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.WriterView.Companion.toWriterView
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class ReviewView(
    @Schema(description = "리뷰 식별자")
    val id: UUID,
    @Schema(description = "평점 (0~5점)")
    val rating: Int,
    @Schema(description = "내용")
    val content: String,
    @Schema(description = "전자책 식별자")
    val ebookId: UUID,
    @Schema(description = "작성자")
    val writer: WriterView,
    @Schema(description = "작성 날짜")
    val writtenDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
) {
    companion object {
        fun ReviewRow.toReviewView(): ReviewView =
            ReviewView(
                id = this.reviewId,
                rating = this.rating,
                content = this.content,
                ebookId = this.ebookId,
                writer = this.toWriterView(),
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )

        fun Review.toReviewView(writer: Member): ReviewView =
            ReviewView(
                id = this.id,
                rating = this.rating,
                content = this.content,
                ebookId = this.ebookId,
                writer = writer.toWriterView(),
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
