package com.devooks.backend.wishlist.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.jooq.tables.references.EBOOK
import com.devooks.backend.jooq.tables.references.RELATED_CATEGORY
import com.devooks.backend.jooq.tables.references.WISHLIST
import com.devooks.backend.wishlist.v1.domain.Wishlist
import com.devooks.backend.wishlist.v1.dto.GetWishlistCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class WishlistQueryRepository : JooqR2dbcRepository() {

    suspend fun findBy(command: GetWishlistCommand): Flow<Wishlist> =
        query {
            select(
                WISHLIST.WISHLIST_ID.`as`("id"),
                WISHLIST.MEMBER_ID,
                WISHLIST.EBOOK_ID,
                WISHLIST.CREATED_DATE,
            ).from(
                WISHLIST
                    .join(EBOOK)
                    .on(EBOOK.EBOOK_ID.eq(WISHLIST.EBOOK_ID))
                    .join(RELATED_CATEGORY)
                    .on(RELATED_CATEGORY.RELATED_CATEGORY_ID.eq(RELATED_CATEGORY.RELATED_CATEGORY_ID))
            ).where(
                WISHLIST.MEMBER_ID.eq(command.memberId)
            ).offset(command.offset).limit(command.limit)
        }.map { it.into(Wishlist::class.java) }

    suspend fun countBy(command: GetWishlistCommand): Flow<Long> =
        query {
            select(
                DSL.count()
            ).from(
                WISHLIST
            ).where(
                WISHLIST.MEMBER_ID.eq(command.memberId)
            )
        }.map { it.into(Long::class.java) }

}
