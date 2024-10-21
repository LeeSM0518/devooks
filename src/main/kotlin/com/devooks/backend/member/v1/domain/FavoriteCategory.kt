package com.devooks.backend.member.v1.domain

import com.devooks.backend.member.v1.entity.FavoriteCategoryEntity
import java.util.*

class FavoriteCategory(
    val id: UUID,
    val categoryId: UUID,
    val memberId: UUID
) {

    companion object {
        fun FavoriteCategoryEntity.toDomain() = FavoriteCategory(id!!, categoryId, favoriteMemberId)
    }
}