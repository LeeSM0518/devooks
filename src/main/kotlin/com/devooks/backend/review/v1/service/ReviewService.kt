package com.devooks.backend.review.v1.service

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.CreateReviewCommand
import com.devooks.backend.review.v1.dto.CreateReviewCommentCommand
import com.devooks.backend.review.v1.dto.DeleteReviewCommand
import com.devooks.backend.review.v1.dto.GetReviewsCommand
import com.devooks.backend.review.v1.dto.ModifyReviewCommand
import com.devooks.backend.review.v1.entity.ReviewEntity
import com.devooks.backend.review.v1.error.ReviewError
import com.devooks.backend.review.v1.repository.ReviewQueryRepository
import com.devooks.backend.review.v1.repository.ReviewRepository
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val reviewQueryRepository: ReviewQueryRepository,
) {
    suspend fun create(command: CreateReviewCommand): Review {
        validateCreateReview(command)
        val entity = ReviewEntity(
            rating = command.rating,
            content = command.content,
            ebookId = command.ebookId,
            writerMemberId = command.requesterId,
        )
        return reviewRepository.save(entity).toDomain()
    }

    suspend fun get(command: GetReviewsCommand): List<Review> =
        reviewQueryRepository.findBy(command).toList()

    suspend fun modify(command: ModifyReviewCommand): Review =
        findById(command.reviewId)
            .also { review -> validateRequesterId(review, command.requesterId) }
            .copy(rating = command.rating, content = command.content, modifiedDate = Instant.now())
            .let { reviewRepository.save(it) }
            .toDomain()

    suspend fun delete(command: DeleteReviewCommand) {
        findById(command.reviewId)
            .also { review -> validateRequesterId(review, command.requesterId) }
            .also { review -> reviewRepository.delete(review) }
    }

    private fun validateRequesterId(
        review: ReviewEntity,
        requesterId: UUID,
    ) {
        review
            .takeIf { it.writerMemberId == requesterId }
            ?: throw ReviewError.FORBIDDEN_MODIFY_REVIEW.exception
    }

    suspend fun validate(command: CreateReviewCommentCommand) {
        findById(command.reviewId)
    }

    suspend fun findById(reviewId: UUID): ReviewEntity =
        reviewRepository
            .findById(reviewId)
            ?: throw ReviewError.NOT_FOUND_REVIEW.exception

    private suspend fun validateCreateReview(command: CreateReviewCommand) {
        reviewRepository
            .existsByEbookIdAndWriterMemberId(command.ebookId, command.requesterId)
            .takeIf { it.not() }
            ?: throw ReviewError.DUPLICATE_REVIEW.exception
    }
}
