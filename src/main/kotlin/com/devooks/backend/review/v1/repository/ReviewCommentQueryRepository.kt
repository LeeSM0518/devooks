package com.devooks.backend.review.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.jooq.tables.references.MEMBER
import com.devooks.backend.jooq.tables.references.REVIEW_COMMENT
import com.devooks.backend.review.v1.dto.GetReviewCommentsCommand
import com.devooks.backend.review.v1.dto.ReviewCommentRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class ReviewCommentQueryRepository : JooqR2dbcRepository() {

    suspend fun findBy(command: GetReviewCommentsCommand): Flow<ReviewCommentRow> =
        query {
            select(
                REVIEW_COMMENT.REVIEW_COMMENT_ID,
                REVIEW_COMMENT.CONTENT,
                REVIEW_COMMENT.REVIEW_ID,
                REVIEW_COMMENT.WRITER_MEMBER_ID,
                MEMBER.MEMBER_ID,
                MEMBER.NICKNAME,
                MEMBER.PROFILE_IMAGE_PATH,
                REVIEW_COMMENT.WRITTEN_DATE,
                REVIEW_COMMENT.MODIFIED_DATE
            ).from(
                REVIEW_COMMENT.join(MEMBER).on(REVIEW_COMMENT.WRITER_MEMBER_ID.eq(MEMBER.MEMBER_ID))
            ).where(
                REVIEW_COMMENT.REVIEW_ID.eq(command.reviewId)
            ).offset(command.offset).limit(command.limit)
        }.map {
            it.into(ReviewCommentRow::class.java)
        }

}
