package com.devooks.backend.category.v1.service

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.domain.Category.Companion.toDomain
import com.devooks.backend.category.v1.error.CategoryError
import com.devooks.backend.category.v1.repository.CategoryRepository
import java.util.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {

    suspend fun getAll(): List<Category> =
        categoryRepository
            .findAll()
            .map { it.toDomain() }
            .toList()

    suspend fun getAll(categoryIds: List<UUID>): List<Category> =
        categoryRepository
            .findAllById(categoryIds)
            .takeIf { it.count() == categoryIds.size }
            ?.map { it.toDomain() }
            ?.toList()
            ?: throw CategoryError.NOT_FOUND_CATEGORY_BY_ID.exception

}
