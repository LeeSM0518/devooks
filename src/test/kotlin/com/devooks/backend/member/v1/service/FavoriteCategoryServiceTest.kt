package com.devooks.backend.member.v1.service

import com.devooks.backend.category.v1.domain.Category.Companion.toDomain
import com.devooks.backend.category.v1.entity.CategoryEntity
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.FavoriteCategoryRepository
import com.devooks.backend.member.v1.repository.MemberRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class FavoriteCategoryServiceTest @Autowired constructor(
    private val favoriteCategoryService: FavoriteCategoryService,
    private val favoriteCategoryRepository: FavoriteCategoryRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
) {

    lateinit var savedMember: MemberEntity
    lateinit var savedCategory: CategoryEntity

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        savedMember = memberRepository.save(MemberEntity(nickname = "nickname"))
        savedCategory = categoryRepository.save(CategoryEntity(name = "category"))
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        favoriteCategoryRepository.deleteAll()
        memberRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    @Test
    fun `관심있는 카테고리를 등록할 수 있다`(): Unit = runBlocking {
        // given
        val categories = listOf(savedCategory.toDomain())
        val memberId = savedMember.id!!

        // when
        val favoriteCategoryList = favoriteCategoryService.save(categories, memberId)

        // then
        val expected = favoriteCategoryRepository.findAll().toList()[0]
        val actual = favoriteCategoryList[0]
        assertThat(actual.id).isEqualTo(expected.id)
        assertThat(actual.categoryId).isEqualTo(expected.categoryId)
        assertThat(actual.memberId).isEqualTo(expected.favoriteMemberId)
    }
}