package com.devooks.backend.review.v1.repository

import com.devooks.backend.review.v1.entity.ReviewEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : CoroutineCrudRepository<ReviewEntity, UUID> {
    suspend fun existsByEbookIdAndWriterMemberId(ebookId: UUID, writerMemberId: UUID): Boolean
}
