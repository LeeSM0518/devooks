package com.devooks.backend.category.v1.dto

import com.devooks.backend.category.v1.domain.Category

data class GetCategoriesResponse(
    val categories: List<CategoryDto>,
) {
    companion object {
        fun List<Category>.toResponse() =
            GetCategoriesResponse(map { CategoryDto(it.id, it.name) })
    }
}
