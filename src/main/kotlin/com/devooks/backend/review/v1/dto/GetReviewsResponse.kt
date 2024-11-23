package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewView.Companion.toReviewView

data class GetReviewsResponse(
    val reviews: List<ReviewView>,
) {
    companion object {
        fun List<Review>.toGetReviewsResponse() =
            GetReviewsResponse(reviews = this.map { it.toReviewView() })
    }
}
