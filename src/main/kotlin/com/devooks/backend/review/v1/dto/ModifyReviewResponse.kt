package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewDto.Companion.toDto

class ModifyReviewResponse(
    val review: ReviewDto
) {
    companion object {
        fun Review.toModifyReviewResponse() = ModifyReviewResponse(this.toDto())
    }
}
