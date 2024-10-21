package com.devooks.backend.category.v1.domain

import com.devooks.backend.category.v1.entity.CategoryEntity
import java.util.*

class Category(
    val id: UUID,
    val name: String,
) {
    companion object {
        fun CategoryEntity.toDomain() = Category(id = this.id!!, name = this.name)
    }
}