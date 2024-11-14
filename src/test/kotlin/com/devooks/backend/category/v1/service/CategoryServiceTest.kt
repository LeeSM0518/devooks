package com.devooks.backend.category.v1.service

import com.devooks.backend.category.v1.entity.CategoryEntity
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.config.IntegrationTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class CategoryServiceTest @Autowired constructor(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
) {

    lateinit var category: CategoryEntity

    @Test
    fun `카테고리를 조회할 수 있다`(): Unit = runBlocking {
        // given
        val categoryEntity = categoryRepository.findAll().toList()[0]
        val categoryIds = listOf(categoryEntity.id!!)

        // when
        val categories = categoryService.getAll(categoryIds)

        // then
        assertThat(categories[0].name).isEqualTo(categoryEntity.name)
    }
}
