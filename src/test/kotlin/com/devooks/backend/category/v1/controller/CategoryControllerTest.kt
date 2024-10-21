package com.devooks.backend.category.v1.controller

import com.devooks.backend.auth.v1.domain.OauthType
import com.devooks.backend.auth.v1.repository.OauthInfoRepository
import com.devooks.backend.auth.v1.repository.RefreshTokenRepository
import com.devooks.backend.category.v1.dto.GetCategoriesResponse
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.dto.SignUpRequest
import com.devooks.backend.member.v1.dto.SignUpResponse
import com.devooks.backend.member.v1.repository.FavoriteCategoryRepository
import com.devooks.backend.member.v1.repository.MemberInfoRepository
import com.devooks.backend.member.v1.repository.MemberRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

    private val expectedCategory = "category"

    @BeforeEach
    fun setup(): Unit = runBlocking {
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER.name,
            nickname = "nickname",
            favoriteCategories = listOf(expectedCategory)
        )

        webTestClient
            .post()
            .uri("/api/v1/members/signup")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<SignUpResponse>()
            .returnResult()
            .responseBody!!
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        refreshTokenRepository.deleteAll()
        favoriteCategoryRepository.deleteAll()
        memberRepository.deleteAll()
        oauthInfoRepository.deleteAll()
        categoryRepository.deleteAll()
        memberInfoRepository.deleteAll()
    }

    @Test
    fun `카테고리 목록을 조회할 수 있다`(): Unit = runBlocking {
        val categories = webTestClient
            .get()
            .uri("/api/v1/categories?page=1&count=10")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetCategoriesResponse>()
            .returnResult()
            .responseBody!!
            .categories

        assertThat(categories[0].name).isEqualTo(expectedCategory)
    }

}