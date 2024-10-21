package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.ReviewComment
import com.devooks.backend.review.v1.dto.ReviewCommentDto.Companion.toDto

data class GetReviewCommentsResponse(
    val reviewComments: List<ReviewCommentDto>,
) {
    companion object {
        fun List<ReviewComment>.toGetReviewCommentsResponse() =
            GetReviewCommentsResponse(map { it.toDto() })
    }
}
