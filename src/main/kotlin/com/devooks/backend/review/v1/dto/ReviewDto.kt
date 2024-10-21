package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
import java.time.Instant
import java.util.*

data class ReviewDto(
    val id: UUID,
    val rating: Int,
    val content: String,
    val ebookId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant,
    val modifiedDate: Instant,
) {
    companion object {
        fun Review.toDto(): ReviewDto =
            ReviewDto(
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
