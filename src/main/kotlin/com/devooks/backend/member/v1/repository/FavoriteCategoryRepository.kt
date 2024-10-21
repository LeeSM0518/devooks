package com.devooks.backend.member.v1.repository

import com.devooks.backend.category.v1.entity.CategoryEntity
import com.devooks.backend.member.v1.entity.FavoriteCategoryEntity
import java.util.*
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FavoriteCategoryRepository : CoroutineCrudRepository<FavoriteCategoryEntity, UUID> {

    suspend fun deleteAllByFavoriteMemberId(memberId: UUID)

    @Query("""
        SELECT c.* 
        FROM favorite_category f, category c 
        WHERE f.category_id = c.category_id 
        AND f.favorite_member_id = :memberId
    """)
    suspend fun findAllByFavoriteMemberId(memberId: UUID): List<CategoryEntity>
}