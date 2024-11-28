package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.review.v1.domain.ReviewComment
import com.devooks.backend.review.v1.dto.ReviewCommentView.Companion.toReviewCommentView

data class CreateReviewCommentResponse(
    val reviewComment: ReviewCommentView,
) {
    companion object {
        fun ReviewComment.toCreateReviewCommentResponse(member: Member) =
            CreateReviewCommentResponse(this.toReviewCommentView(member))
    }
}
