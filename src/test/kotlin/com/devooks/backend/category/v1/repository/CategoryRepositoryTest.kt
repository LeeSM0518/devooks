package com.devooks.backend.category.v1.repository

import com.devooks.backend.category.v1.entity.CategoryEntity
import com.devooks.backend.config.IntegrationTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable

@IntegrationTest
internal class CategoryRepositoryTest @Autowired constructor(
    private val categoryRepository: CategoryRepository,
) {

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        categoryRepository.deleteAll()
    }

    @Test
    fun `카테고리를 조회할 수 있다`(): Unit = runBlocking {
        // given
        val entity = CategoryEntity(name = "category")
        categoryRepository.save(entity)

        // when
        val categories = categoryRepository
            .findAllByNameLikeIgnoreCase("%c%", Pageable.ofSize(1).withPage(0))
            .toList()

        // then
        assertThat(categories.first().name).isEqualTo(entity.name)
    }

    @Test
    fun `카테고리를 전체 조회할 수 있다`(): Unit = runBlocking {
        // given
        val entity = CategoryEntity(name = "category")
        categoryRepository.save(entity)

        // when
        val categories = categoryRepository
            .findAllByNameLikeIgnoreCase("%c%")
            .toList()

        // then
        assertThat(categories.first().name).isEqualTo(entity.name)
    }

}