package com.devooks.backend.review.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.util.*

data class ModifyReviewCommentRequest(
    @field:NotBlank
    @Schema(description = "내용", required = true)
    val content: String,
) {
    fun toCommand(commentId: UUID, requesterId: UUID): ModifyReviewCommentCommand =
        ModifyReviewCommentCommand(
            content = content,
            commentId = commentId,
            requesterId = requesterId,
        )
}
