package com.devooks.backend.ebook.v1.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ReviewView(
    @Schema(description = "평점")
    val rating: Double,
    @Schema(description = "개수", implementation = Int::class)
    val count: Int
)
