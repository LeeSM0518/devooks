package com.devooks.backend.review.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.util.*

data class ModifyReviewRequest(
    @field:Min(0)
    @field:Max(5)
    @Schema(description = "평점 (0~5점)", required = true)
    val rating: Int?,
    @field:Size(min = 1)
    @Schema(description = "내용", required = true)
    val content: String?,
) {
    fun toCommand(reviewId: UUID, requesterId: UUID): ModifyReviewCommand =
        ModifyReviewCommand(
            reviewId = reviewId,
            rating = rating,
            content = content,
            requesterId = requesterId
        )
}
