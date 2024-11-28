package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewView.Companion.toReviewView

data class CreateReviewResponse(
    val review: ReviewView,
) {
    companion object {
        fun Review.toCreateReviewResponse() = CreateReviewResponse(this.toReviewView())
    }
}
