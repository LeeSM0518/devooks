package com.devooks.backend.ebook.v1.service

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.domain.Category.Companion.toDomain
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import com.devooks.backend.ebook.v1.entity.RelatedCategoryEntity
import com.devooks.backend.ebook.v1.repository.RelatedCategoryRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class RelatedCategoryService(
    private val relatedCategoryRepository: RelatedCategoryRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend fun save(categoryList: List<Category>, ebook: Ebook) =
        categoryList
            .map { category -> RelatedCategoryEntity(ebookId = ebook.id, categoryId = category.id) }
            .let { relatedCategoryRepository.saveAll(it) }
            .toList()

    suspend fun modify(command: ModifyEbookCommand, ebook: Ebook): List<Category> {
        return if (command.isChangedRelatedCategoryIdList) {
            val categoryList =
                categoryRepository.findAllById(command.relatedCategoryIdList!!).map { it.toDomain() }.toList()
            relatedCategoryRepository.deleteAllByEbookId(ebook.id)
            save(categoryList, ebook)
            categoryList
        } else {
            relatedCategoryRepository
                .findAllByEbookId(command.ebookId)
                .map { it.categoryId }
                .let { categoryRepository.findAllById(it) }
                .map { it.toDomain() }
                .toList()
        }
    }
}
