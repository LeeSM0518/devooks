package com.devooks.backend.category.v1.repository

import com.devooks.backend.config.IntegrationTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable

@IntegrationTest
internal class CategoryRepositoryTest @Autowired constructor(
    private val categoryRepository: CategoryRepository,
) {

    @Test
    fun `카테고리를 조회할 수 있다`(): Unit = runBlocking {
        // given
        val expectedName = categoryRepository.findAll().toList()[0].name

        // when
        val categories = categoryRepository
            .findAllByNameLikeIgnoreCase(
                name = "%$expectedName%",
                pageable = Pageable.ofSize(1).withPage(0)
            )
            .toList()

        // then
        assertThat(categories.first().name).isEqualTo(expectedName)
    }

    @Test
    fun `카테고리를 전체 조회할 수 있다`(): Unit = runBlocking {
        // given
        val expectedName = categoryRepository.findAll().toList()[0].name

        // when
        val categories = categoryRepository
            .findAllByNameLikeIgnoreCase("%$expectedName%")
            .toList()

        // then
        assertThat(categories.first().name).isEqualTo(expectedName)
    }

}
