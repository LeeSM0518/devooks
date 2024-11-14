package com.devooks.backend.category.v1.domain

import com.devooks.backend.category.v1.entity.CategoryEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

class Category(
    @Schema(description = "카테고리 식별자")
    val id: UUID,
    @Schema(description = "카테고리 이름")
    val name: String,
) {
    companion object {
        fun CategoryEntity.toDomain() = Category(id = this.id!!, name = this.name)
    }
}
