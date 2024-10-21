package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.ReviewComment
import com.devooks.backend.review.v1.dto.ReviewCommentDto.Companion.toDto

data class ModifyReviewCommentResponse(
    val reviewComment: ReviewCommentDto
) {
    companion object {
        fun ReviewComment.toModifyReviewCommentResponse() =
            ModifyReviewCommentResponse(toDto())
    }
}
