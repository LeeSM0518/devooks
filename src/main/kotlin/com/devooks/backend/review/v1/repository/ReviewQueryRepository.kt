package com.devooks.backend.review.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.jooq.tables.Review.Companion.REVIEW
import com.devooks.backend.jooq.tables.references.EBOOK
import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.GetReviewsCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class ReviewQueryRepository : JooqR2dbcRepository() {

    suspend fun findBy(command: GetReviewsCommand): Flow<Review> =
        query {
            select(
                REVIEW.REVIEW_ID.`as`("id"),
                REVIEW.RATING,
                REVIEW.CONTENT,
                REVIEW.EBOOK_ID,
                REVIEW.WRITER_MEMBER_ID,
                REVIEW.WRITTEN_DATE,
                REVIEW.MODIFIED_DATE
            ).from(
                REVIEW
                    .join(EBOOK).on(EBOOK.EBOOK_ID.eq(REVIEW.EBOOK_ID))
            ).where(
                REVIEW.EBOOK_ID.eq(command.ebookId)
            ).orderBy(
                REVIEW.WRITTEN_DATE.desc(),
            ).offset(command.offset).limit(command.limit)
        }.map { it.into(Review::class.java) }

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
