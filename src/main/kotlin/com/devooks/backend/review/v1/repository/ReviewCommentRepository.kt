package com.devooks.backend.review.v1.repository

import com.devooks.backend.review.v1.entity.ReviewCommentEntity
import java.util.*
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewCommentRepository : CoroutineCrudRepository<ReviewCommentEntity, UUID> {

    suspend fun findAllByReviewId(reviewId: UUID, pageable: Pageable): Flow<ReviewCommentEntity>
}
