package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.review.v1.domain.ReviewComment
import com.devooks.backend.review.v1.dto.ReviewCommentView.Companion.toReviewCommentView

data class ModifyReviewCommentResponse(
    val reviewComment: ReviewCommentView,
) {
    companion object {
        fun ReviewComment.toModifyReviewCommentResponse(member: Member) =
            ModifyReviewCommentResponse(this.toReviewCommentView(member))
    }
}
