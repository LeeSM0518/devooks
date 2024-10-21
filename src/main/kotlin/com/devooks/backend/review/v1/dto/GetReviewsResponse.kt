package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewDto.Companion.toDto

data class GetReviewsResponse(
    val reviews: List<ReviewDto>,
) {
    companion object {
        fun List<Review>.toGetReviewsResponse() =
            GetReviewsResponse(reviews = this.map { it.toDto() })
    }
}
