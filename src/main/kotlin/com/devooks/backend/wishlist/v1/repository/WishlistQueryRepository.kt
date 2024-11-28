package com.devooks.backend.wishlist.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.jooq.tables.references.EBOOK
import com.devooks.backend.jooq.tables.references.RELATED_CATEGORY
import com.devooks.backend.jooq.tables.references.WISHLIST
import com.devooks.backend.wishlist.v1.dto.GetWishlistCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class WishlistQueryRepository : JooqR2dbcRepository() {

    suspend fun countBy(command: GetWishlistCommand): Flow<Long> =
        query {
            select(
                DSL.count()
            ).from(
                WISHLIST
                    .join(EBOOK).on(WISHLIST.EBOOK_ID.eq(EBOOK.EBOOK_ID))
                    .join(RELATED_CATEGORY).on(EBOOK.EBOOK_ID.eq(RELATED_CATEGORY.EBOOK_ID))
            ).where(
                WISHLIST.MEMBER_ID.eq(command.memberId).and(EBOOK.DELETED_DATE.isNull)
                    .let { where ->
                        command.categoryIdList?.let { categoryIdList ->
                            where.and(RELATED_CATEGORY.CATEGORY_ID.`in`(categoryIdList))
                        } ?: where
                    }
            )
        }.map { it.into(Long::class.java) }

}
