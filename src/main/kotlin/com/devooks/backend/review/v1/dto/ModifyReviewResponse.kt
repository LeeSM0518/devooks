package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewView.Companion.toReviewView

class ModifyReviewResponse(
    val review: ReviewView
) {
    companion object {
        fun Review.toModifyReviewResponse() = ModifyReviewResponse(this.toReviewView())
    }
}
