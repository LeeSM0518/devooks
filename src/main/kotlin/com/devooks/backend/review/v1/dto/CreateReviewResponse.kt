package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewDto.Companion.toDto

data class CreateReviewResponse(
    val review: ReviewDto,
) {
    companion object {
        fun Review.toCreateReviewResponse() = CreateReviewResponse(this.toDto())
    }
}
