package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.ReviewView.Companion.toReviewView

class ModifyReviewResponse(
    val review: ReviewView,
) {
    companion object {
        fun Review.toModifyReviewResponse(member: Member) =
            ModifyReviewResponse(this.toReviewView(member))
    }
}
