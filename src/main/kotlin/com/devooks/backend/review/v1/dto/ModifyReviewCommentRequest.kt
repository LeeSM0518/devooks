package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateReviewCommentId
import com.devooks.backend.review.v1.error.validateReviewContent
import java.util.*

data class ModifyReviewCommentRequest(
    val content: String?,
) {
    fun toCommand(commentId: String, requesterId: UUID): ModifyReviewCommentCommand =
        ModifyReviewCommentCommand(
            content = content.validateReviewContent(),
            commentId = commentId.validateReviewCommentId(),
            requesterId = requesterId,
        )
}
