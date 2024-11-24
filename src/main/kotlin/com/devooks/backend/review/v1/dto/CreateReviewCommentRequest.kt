package com.devooks.backend.review.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.util.*

data class CreateReviewCommentRequest(
    @Schema(description = "리뷰 식별자", required = true, implementation = UUID::class)
    val reviewId: UUID,
    @field:NotBlank
    @Schema(description = "내용", required = true)
    val content: String,
) {
    fun toCommand(requesterId: UUID): CreateReviewCommentCommand =
        CreateReviewCommentCommand(
            reviewId = reviewId,
            content = content,
            requesterId = requesterId,
        )
}
