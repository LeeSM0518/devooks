package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewView.Companion.toReviewView

data class CreateReviewResponse(
    val review: ReviewView,
) {
    companion object {
        fun Review.toCreateReviewResponse(member: Member) =
            CreateReviewResponse(this.toReviewView(member))
    }
}
