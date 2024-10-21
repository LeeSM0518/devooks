package com.devooks.backend.category.v1.dto

import com.devooks.backend.category.v1.domain.Category
import java.util.*

data class CategoryDto(
    val id: UUID,
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