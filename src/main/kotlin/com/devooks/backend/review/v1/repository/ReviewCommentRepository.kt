package com.devooks.backend.review.v1.repository

import com.devooks.backend.review.v1.entity.ReviewCommentEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewCommentRepository : CoroutineCrudRepository<ReviewCommentEntity, UUID> {

    suspend fun countByReviewId(reviewId: UUID): Long
}
