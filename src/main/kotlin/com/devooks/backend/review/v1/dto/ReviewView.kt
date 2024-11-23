package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
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
    @Schema(description = "작성자 회원 식별자")
    val writerMemberId: UUID,
    @Schema(description = "작성 날짜")
    val writtenDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
) {
    companion object {
        fun Review.toReviewView(): ReviewView =
            ReviewView(
                id = this.id,
                rating = this.rating,
                content = this.content,
                ebookId = this.ebookId,
                writerMemberId = this.writerMemberId,
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
