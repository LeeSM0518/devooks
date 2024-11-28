package com.devooks.backend.review.v1.service

import com.devooks.backend.review.v1.domain.ReviewComment
import com.devooks.backend.review.v1.dto.CreateReviewCommentCommand
import com.devooks.backend.review.v1.dto.DeleteReviewCommentCommand
import com.devooks.backend.review.v1.dto.GetReviewCommentsCommand
import com.devooks.backend.review.v1.dto.ModifyReviewCommentCommand
import com.devooks.backend.review.v1.dto.ReviewCommentRow
import com.devooks.backend.review.v1.entity.ReviewCommentEntity
import com.devooks.backend.review.v1.error.ReviewError
import com.devooks.backend.review.v1.repository.ReviewCommentQueryRepository
import com.devooks.backend.review.v1.repository.ReviewCommentRepository
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class ReviewCommentService(
    private val reviewCommentRepository: ReviewCommentRepository,
    private val reviewCommentQueryRepository: ReviewCommentQueryRepository,
) {
    suspend fun create(command: CreateReviewCommentCommand): ReviewComment {
        val entity = ReviewCommentEntity(
            reviewId = command.reviewId,
            content = command.content,
            writerMemberId = command.requesterId,
        )
        return reviewCommentRepository.save(entity).toDomain()
    }

    suspend fun get(command: GetReviewCommentsCommand): Page<ReviewCommentRow> {
        val reviewComments = reviewCommentQueryRepository.findBy(command)
        val count = reviewCommentRepository.countByReviewId(command.reviewId)
        return PageImpl(reviewComments.toList(), command.pageable, count)
    }

    suspend fun modify(command: ModifyReviewCommentCommand): ReviewComment =
        findBy(command.commentId)
            .also { reviewComment -> validateRequesterId(reviewComment, command.requesterId) }
            .copy(content = command.content, modifiedDate = Instant.now())
            .let { reviewCommentRepository.save(it) }
            .toDomain()


    suspend fun delete(command: DeleteReviewCommentCommand) {
        findBy(command.commentId)
            .also { comment -> validateRequesterId(comment, command.requesterId) }
            .also { comment -> reviewCommentRepository.delete(comment) }
    }

    private fun validateRequesterId(reviewComment: ReviewCommentEntity, requesterId: UUID) {
        reviewComment
            .takeIf { it.writerMemberId == requesterId }
            ?: throw ReviewError.FORBIDDEN_MODIFY_REVIEW_COMMENT.exception
    }

    private suspend fun findBy(reviewCommentId: UUID): ReviewCommentEntity =
        reviewCommentRepository
            .findById(reviewCommentId)
            ?: throw ReviewError.NOT_FOUND_REVIEW_COMMENT.exception
}
