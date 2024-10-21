package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.ReviewComment
import java.time.Instant
import java.util.*

data class ReviewCommentDto(
    val id: UUID,
    val content: String,
    val reviewId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
) {
    companion object {
        fun ReviewComment.toDto(): ReviewCommentDto =
            ReviewCommentDto(
                id = this.id,
                content = this.content,
                reviewId = this.reviewId,
                writerMemberId = this.writerMemberId,
                writtenDate = this.writtenDate,
                modifiedDate = this.modifiedDate,
            )
    }
}
