package com.devooks.backend.review.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.util.*

data class CreateReviewRequest(
    @Schema(description = "전자책 식별자", implementation = UUID::class, required = true)
    val ebookId: UUID,
    @field:Min(0)
    @field:Max(5)
    @Schema(description = "평점 (0~5점)", required = true)
    val rating: Int,
    @field:NotBlank
    @Schema(description = "내용", required = true)
    val content: String
) {
    fun toCommand(requesterId: UUID): CreateReviewCommand =
        CreateReviewCommand(
            ebookId = ebookId,
            rating = rating,
            content = content,
            requesterId = requesterId
        )
}
