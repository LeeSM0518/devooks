package com.devooks.backend.wishlist.v1.repository

import com.devooks.backend.wishlist.v1.entity.WishlistEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WishlistCrudRepository : CoroutineCrudRepository<WishlistEntity, UUID> {
    suspend fun findByMemberIdAndEbookId(memberId: UUID, ebookId: UUID): WishlistEntity?
}
