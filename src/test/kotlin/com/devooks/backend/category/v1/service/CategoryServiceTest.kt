package com.devooks.backend.category.v1.service

import com.devooks.backend.category.v1.entity.CategoryEntity
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.config.IntegrationTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class CategoryServiceTest @Autowired constructor(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository
) {

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        categoryRepository.deleteAll()
    }

    @Test
    fun `카테고리를 저장할 수 있다`(): Unit = runBlocking {
        // given
        val categoryNames = listOf("category")

        // when
        val categories = categoryService.save(categoryNames)
        val entity = categoryRepository.findByNameIsIgnoreCase("category")

        // then
        assertThat(categories[0].name).isEqualTo(entity!!.name)
    }

    @Test
    fun `카테고리가 이미 존재할 경우 저장하지 않는다`(): Unit = runBlocking {
        // given
        val categoryNames = listOf("category")
        val categoryEntity = categoryRepository.save(CategoryEntity(name = categoryNames[0]))

        // when
        val categories = categoryService.save(categoryNames)

        // then
        assertThat(categoryRepository.count()).isOne()
        assertThat(categories[0].id).isEqualTo(categoryEntity.id)
        assertThat(categories[0].name).isEqualTo(categoryEntity.name)
    }
}