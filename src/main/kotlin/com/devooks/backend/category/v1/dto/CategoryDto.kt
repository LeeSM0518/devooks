package com.devooks.backend.category.v1.dto

import com.devooks.backend.category.v1.domain.Category
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CategoryDto(
    @Schema(description = "카테고리 식별자")
    val id: UUID,
    @Schema(description = "카테고리 이름")
    val name: String,
) {
    companion object {
        fun Category.toDto() =
            CategoryDto(
                id = id,
                name = name
            )
    }
}
