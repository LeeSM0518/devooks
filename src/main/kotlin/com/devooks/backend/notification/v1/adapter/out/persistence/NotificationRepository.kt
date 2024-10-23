package com.devooks.backend.notification.v1.adapter.out.persistence

import java.util.*
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : CoroutineCrudRepository<NotificationEntity, UUID> {

    suspend fun countByReceiverIdAndCheckedIsFalse(
        receiverId: UUID,
    ): Long

    suspend fun findAllByReceiverIdOrderByNotifiedDateDesc(
        receiverId: UUID,
        pageable: Pageable,
    ): Flow<NotificationEntity>

    suspend fun countByReceiverId(
        receiverId: UUID,
    ): Long

    suspend fun findAllByReceiverIdAndCheckedIsFalse(receiverId: UUID): Flow<NotificationEntity>
}