package com.devooks.backend.auth.v1.repository

import com.devooks.backend.auth.v1.entity.RefreshTokenEntity
import java.util.UUID
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : CoroutineCrudRepository<RefreshTokenEntity, UUID> {
    suspend fun findByMemberId(memberId: UUID): RefreshTokenEntity?
    suspend fun deleteByMemberId(memberId: UUID)
}
