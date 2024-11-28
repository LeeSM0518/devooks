package com.devooks.backend.review.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.jooq.tables.Review.Companion.REVIEW
import com.devooks.backend.jooq.tables.references.MEMBER
import com.devooks.backend.jooq.tables.references.REVIEW_COMMENT
import com.devooks.backend.review.v1.dto.GetReviewsCommand
import com.devooks.backend.review.v1.dto.ReviewRow
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class ReviewQueryRepository : JooqR2dbcRepository() {

    suspend fun findBy(command: GetReviewsCommand): Flow<ReviewRow> =
        query {
            val reviewCountSubQuery =
                select(
                    REVIEW_COMMENT.REVIEW_ID,
                    DSL.count().`as`("comment_count")
                ).from(
                    REVIEW_COMMENT
                ).groupBy(
                    REVIEW_COMMENT.REVIEW_ID,
                ).asTable("review_comment")

            select(
                REVIEW.REVIEW_ID,
                REVIEW.CONTENT,
                REVIEW.RATING,
                REVIEW.EBOOK_ID,
                MEMBER.MEMBER_ID,
                MEMBER.NICKNAME,
                MEMBER.PROFILE_IMAGE_PATH,
                REVIEW.WRITTEN_DATE,
                REVIEW.MODIFIED_DATE,
                DSL.coalesce(reviewCountSubQuery.field("comment_count"), 0)
                    .`as`("comment_count")
            ).from(
                REVIEW
                    .join(MEMBER).on(REVIEW.WRITER_MEMBER_ID.eq(MEMBER.MEMBER_ID))
                    .leftJoin(reviewCountSubQuery).on(
                        REVIEW.REVIEW_ID.eq(reviewCountSubQuery.field("review_id", UUID::class.java))
                    )
            ).where(
                REVIEW.EBOOK_ID.eq(command.ebookId)
            ).orderBy(
                REVIEW.WRITTEN_DATE.desc(),
            ).offset(command.offset).limit(command.limit)
        }.map { it.into(ReviewRow::class.java) }

    suspend fun countBy(command: GetReviewsCommand): Flow<Long> =
        query {
            select(
                DSL.count()
            ).from(
                REVIEW
            ).where(
                REVIEW.EBOOK_ID.eq(command.ebookId)
            )
        }.map { it.into(Long::class.java) }

}
