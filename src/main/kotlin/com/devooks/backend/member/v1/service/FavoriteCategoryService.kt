package com.devooks.backend.member.v1.service

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.domain.Category.Companion.toDomain
import com.devooks.backend.member.v1.domain.FavoriteCategory
import com.devooks.backend.member.v1.domain.FavoriteCategory.Companion.toDomain
import com.devooks.backend.member.v1.entity.FavoriteCategoryEntity
import com.devooks.backend.member.v1.repository.FavoriteCategoryRepository
import java.util.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class FavoriteCategoryService(
    private val favoriteCategoryRepository: FavoriteCategoryRepository,
) {

    suspend fun save(categories: List<Category>, memberId: UUID): List<FavoriteCategory> =
        categories
            .asFlow()
            .map { FavoriteCategoryEntity(favoriteMemberId = memberId, categoryId = it.id) }
            .map { favoriteCategoryRepository.save(it) }
            .map { it.toDomain() }
            .toList()

    suspend fun deleteByMemberId(memberId: UUID) {
        favoriteCategoryRepository.deleteAllByFavoriteMemberId(memberId)
    }

    suspend fun findByMemberId(memberId: UUID): List<Category> =
        favoriteCategoryRepository
            .findAllByFavoriteMemberId(memberId)
            .map { it.toDomain() }

}