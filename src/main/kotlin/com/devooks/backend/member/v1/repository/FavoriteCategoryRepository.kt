package com.devooks.backend.member.v1.repository

import com.devooks.backend.member.v1.entity.FavoriteCategoryEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FavoriteCategoryRepository : CoroutineCrudRepository<FavoriteCategoryEntity, UUID> {

    suspend fun deleteAllByFavoriteMemberId(memberId: UUID)

    suspend fun findAllByFavoriteMemberId(memberId: UUID): List<FavoriteCategoryEntity>
}
