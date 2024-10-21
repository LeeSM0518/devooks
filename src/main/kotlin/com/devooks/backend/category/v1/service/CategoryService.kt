package com.devooks.backend.category.v1.service

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.domain.Category.Companion.toDomain
import com.devooks.backend.category.v1.dto.GetCategoriesRequest
import com.devooks.backend.category.v1.entity.CategoryEntity
import com.devooks.backend.category.v1.repository.CategoryRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {

    suspend fun get(request: GetCategoriesRequest): List<Category> =
        categoryRepository
            .findAllByNameLikeIgnoreCase(
                name = request.keyword,
                pageable = request.paging.value
            )
            .map { Category(it.id!!, it.name) }
            .toList()

    suspend fun save(categoryNames: List<String>): List<Category> =
        categoryNames
            .asFlow()
            .map { name -> name to categoryRepository.findByNameIsIgnoreCase(name) }
            .map { (name, entity) -> entity ?: categoryRepository.save(CategoryEntity(name = name)) }
            .map { it.toDomain() }
            .toList()

}
