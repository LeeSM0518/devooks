package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateReviewCommentId
import com.devooks.backend.review.v1.error.validateReviewContent
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ModifyReviewCommentRequest(
    @Schema(description = "내용", required = true)
    val content: String?,
) {
    fun toCommand(commentId: String, requesterId: UUID): ModifyReviewCommentCommand =
        ModifyReviewCommentCommand(
            content = content.validateReviewContent(),
            commentId = commentId.validateReviewCommentId(),
            requesterId = requesterId,
        )
}
