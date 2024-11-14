package com.devooks.backend.category.v1.controller

import com.devooks.backend.auth.v1.repository.OauthInfoRepository
import com.devooks.backend.auth.v1.repository.RefreshTokenRepository
import com.devooks.backend.category.v1.domain.Category.Companion.toDomain
import com.devooks.backend.category.v1.dto.CategoryDto.Companion.toDto
import com.devooks.backend.category.v1.dto.GetCategoriesResponse
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.repository.FavoriteCategoryRepository
import com.devooks.backend.member.v1.repository.MemberInfoRepository
import com.devooks.backend.member.v1.repository.MemberRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
internal class CategoryControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val memberRepository: MemberRepository,
    private val memberInfoRepository: MemberInfoRepository,
    private val oauthInfoRepository: OauthInfoRepository,
    private val categoryRepository: CategoryRepository,
    private val favoriteCategoryRepository: FavoriteCategoryRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        refreshTokenRepository.deleteAll()
        favoriteCategoryRepository.deleteAll()
        memberRepository.deleteAll()
        oauthInfoRepository.deleteAll()
        memberInfoRepository.deleteAll()
    }

    @Test
    fun `카테고리 목록을 조회할 수 있다`(): Unit = runBlocking {
        // given
        val foundCategories = categoryRepository.findAll().toList().map { it.toDomain().toDto() }

        // when
        val categories = webTestClient
            .get()
            .uri("/api/v1/categories")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetCategoriesResponse>()
            .returnResult()
            .responseBody!!
            .categories

        // then
        assertThat(foundCategories).containsAll(categories)
    }

}
