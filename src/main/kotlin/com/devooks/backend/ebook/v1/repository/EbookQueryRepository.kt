package com.devooks.backend.ebook.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.ebook.v1.domain.EbookImageType
import com.devooks.backend.ebook.v1.domain.EbookOrder
import com.devooks.backend.ebook.v1.dto.command.GetDetailOfEbookCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookCommand
import com.devooks.backend.ebook.v1.repository.row.EbookDetailRow
import com.devooks.backend.ebook.v1.repository.row.EbookRow
import com.devooks.backend.jooq.tables.references.EBOOK
import com.devooks.backend.jooq.tables.references.EBOOK_IMAGE
import com.devooks.backend.jooq.tables.references.MEMBER
import com.devooks.backend.jooq.tables.references.PDF
import com.devooks.backend.jooq.tables.references.RELATED_CATEGORY
import com.devooks.backend.jooq.tables.references.REVIEW
import com.devooks.backend.jooq.tables.references.WISHLIST
import com.devooks.backend.wishlist.v1.dto.GetWishlistCommand
import java.math.BigDecimal
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.JSON
import org.jooq.Record2
import org.jooq.Record3
import org.jooq.Table
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.key
import org.jooq.impl.SQLDataType
import org.springframework.stereotype.Repository

@Repository
class EbookQueryRepository : JooqR2dbcRepository() {

    suspend fun count(command: GetEbookCommand): Flow<Long> =
        query {
            val relatedCategorySubQuery = getRelatedCategorySubQuery()

            select(
                DSL.count()
            ).from(
                EBOOK
                    .leftJoin(relatedCategorySubQuery).on(
                        EBOOK.EBOOK_ID.eq(relatedCategorySubQuery.field("ebook_id", UUID::class.java))
                    )
            ).where(
                buildConditionsToGetEbooks(
                    title = command.title,
                    sellerMemberId = command.sellerMemberId,
                    ebookIdList = command.ebookIdList,
                    categoryIdList = command.categoryIdList
                )
            )
        }.map { it.into(Long::class.java) }

    suspend fun findWishlistBy(command: GetWishlistCommand): Flow<EbookRow> =
        query {
            val (reviewSubQuery, mainImageSubQuery, relatedCategorySubQuery) = getSubQueries()

            getEbookRowSelectQuery(
                reviewSubQuery, relatedCategorySubQuery, mainImageSubQuery
            ).from(
                EBOOK
                    .join(MEMBER).on(EBOOK.SELLING_MEMBER_ID.eq(MEMBER.MEMBER_ID))
                    .join(WISHLIST).on(
                        EBOOK.EBOOK_ID.eq(WISHLIST.EBOOK_ID).and(WISHLIST.MEMBER_ID.eq(command.memberId))
                    )
                    .leftJoin(reviewSubQuery).on(
                        EBOOK.EBOOK_ID.eq(reviewSubQuery.field("ebook_id", UUID::class.java))
                    )
                    .leftJoin(mainImageSubQuery).on(
                        EBOOK.EBOOK_ID.eq(mainImageSubQuery.field("ebook_id", UUID::class.java))
                    )
                    .leftJoin(relatedCategorySubQuery).on(
                        EBOOK.EBOOK_ID.eq(relatedCategorySubQuery.field("ebook_id", UUID::class.java))
                    )
            ).where(
                buildConditionsToGetEbooks(categoryIdList = command.categoryIdList)
            ).orderBy(
                WISHLIST.CREATED_DATE.desc()
            ).offset(command.offset).limit(command.limit)
        }.map {
            it.into(EbookRow::class.java)
        }

    suspend fun findEbooksBy(command: GetEbookCommand): Flow<EbookRow> =
        query {
            val (reviewSubQuery, mainImageSubQuery, relatedCategorySubQuery) = getSubQueries()

            getEbookRowSelectQuery(
                reviewSubQuery, relatedCategorySubQuery, mainImageSubQuery
            ).from(
                EBOOK
                    .join(MEMBER).on(EBOOK.SELLING_MEMBER_ID.eq(MEMBER.MEMBER_ID))
                    .leftJoin(reviewSubQuery).on(
                        EBOOK.EBOOK_ID.eq(reviewSubQuery.field("ebook_id", UUID::class.java))
                    )
                    .leftJoin(WISHLIST).on(
                        EBOOK.EBOOK_ID.eq(WISHLIST.EBOOK_ID).and(WISHLIST.MEMBER_ID.eq(command.requesterId))
                    )
                    .leftJoin(mainImageSubQuery).on(
                        EBOOK.EBOOK_ID.eq(mainImageSubQuery.field("ebook_id", UUID::class.java))
                    )
                    .leftJoin(relatedCategorySubQuery).on(
                        EBOOK.EBOOK_ID.eq(relatedCategorySubQuery.field("ebook_id", UUID::class.java))
                    )
            ).where(
                buildConditionsToGetEbooks(
                    title = command.title,
                    sellerMemberId = command.sellerMemberId,
                    ebookIdList = command.ebookIdList,
                    categoryIdList = command.categoryIdList
                )
            ).run {
                when (command.orderBy) {
                    EbookOrder.LATEST -> orderBy(EBOOK.CREATED_DATE.desc())
                    EbookOrder.REVIEW -> orderBy(DSL.field("review_rating", Double::class.java).desc())
                }
            }.offset(command.offset).limit(command.limit)
        }.map {
            it.into(EbookRow::class.java)
        }

    suspend fun findEbooksBy(command: GetDetailOfEbookCommand): EbookDetailRow? =
        query {
            val reviewSubQuery = getReviewSubQuery()
            val mainImageSubQuery = getMainImageSubQuery()
            val descriptionImageSubQuery = getDescriptionImageSubQuery()
            val relatedCategorySubQuery = getRelatedCategorySubQuery()

            select(
                EBOOK.EBOOK_ID,
                EBOOK.TITLE,
                EBOOK.PRICE,
                EBOOK.SELLING_MEMBER_ID.`as`("seller_member_id"),
                MEMBER.NICKNAME.`as`("seller_nickname"),
                MEMBER.PROFILE_IMAGE_PATH.`as`("seller_profile_image_path"),
                DSL.coalesce(reviewSubQuery.field("review_rating"), 0.0)
                    .`as`("review_rating"),
                DSL.coalesce(reviewSubQuery.field("review_count"), 0)
                    .`as`("review_count"),
                EBOOK.CREATED_DATE,
                EBOOK.MODIFIED_DATE,
                DSL.coalesce(
                    relatedCategorySubQuery.field("related_category_id_list"),
                    DSL.inline("{}")
                ).`as`("related_category_id_list"),
                WISHLIST.WISHLIST_ID,
                EBOOK.INTRODUCTION,
                EBOOK.TABLE_OF_CONTENTS,
                PDF.PDF_ID,
                PDF.PAGE_COUNT,
                mainImageSubQuery.field("main_image_json_data"),
                DSL.coalesce(
                    descriptionImageSubQuery.field("description_image_json_data"),
                    DSL.inline("[]")
                ).`as`("description_image_json_data"),
            ).from(
                EBOOK
                    .join(PDF).on(EBOOK.PDF_ID.eq(PDF.PDF_ID))
                    .join(MEMBER).on(EBOOK.SELLING_MEMBER_ID.eq(MEMBER.MEMBER_ID))
                    .leftJoin(reviewSubQuery).on(
                        EBOOK.EBOOK_ID.eq(reviewSubQuery.field("ebook_id", UUID::class.java))
                    )
                    .leftJoin(WISHLIST).on(
                        EBOOK.EBOOK_ID.eq(WISHLIST.EBOOK_ID).and(WISHLIST.MEMBER_ID.eq(command.requesterId))
                    )
                    .leftJoin(mainImageSubQuery).on(
                        EBOOK.EBOOK_ID.eq(mainImageSubQuery.field("ebook_id", UUID::class.java))
                    )
                    .leftJoin(descriptionImageSubQuery).on(
                        EBOOK.EBOOK_ID.eq(descriptionImageSubQuery.field("ebook_id", UUID::class.java))
                    )
                    .leftJoin(relatedCategorySubQuery).on(
                        EBOOK.EBOOK_ID.eq(relatedCategorySubQuery.field("ebook_id", UUID::class.java))
                    )
            ).where(
                EBOOK.DELETED_DATE.isNull.and(EBOOK.EBOOK_ID.eq(command.ebookId))
            )
        }.map {
            it.into(EbookDetailRow::class.java)
        }.firstOrNull()

    private fun DSLContext.getEbookRowSelectQuery(
        reviewSubQuery: Table<Record3<UUID?, BigDecimal, Int>>,
        relatedCategorySubQuery: Table<Record2<UUID?, Array<UUID?>>>,
        mainImageSubQuery: Table<Record2<UUID?, JSON>>,
    ) = select(
        EBOOK.EBOOK_ID,
        EBOOK.TITLE,
        EBOOK.PRICE,
        MEMBER.MEMBER_ID.`as`("seller_member_id"),
        MEMBER.NICKNAME.`as`("seller_nickname"),
        MEMBER.PROFILE_IMAGE_PATH.`as`("seller_profile_image_path"),
        DSL.coalesce(reviewSubQuery.field("review_rating"), 0.0)
            .`as`("review_rating"),
        DSL.coalesce(reviewSubQuery.field("review_count"), 0)
            .`as`("review_count"),
        EBOOK.CREATED_DATE,
        EBOOK.MODIFIED_DATE,
        DSL.coalesce(
            relatedCategorySubQuery.field("related_category_id_list"),
            DSL.inline("{}")
        ).`as`("related_category_id_list"),
        WISHLIST.WISHLIST_ID,
        mainImageSubQuery.field("main_image_json_data"),
    )

    private fun DSLContext.getSubQueries(): Triple<Table<Record3<UUID?, BigDecimal, Int>>, Table<Record2<UUID?, JSON>>, Table<Record2<UUID?, Array<UUID?>>>> {
        val reviewSubQuery = getReviewSubQuery()
        val mainImageSubQuery = getMainImageSubQuery()
        val relatedCategorySubQuery = getRelatedCategorySubQuery()
        return Triple(reviewSubQuery, mainImageSubQuery, relatedCategorySubQuery)
    }

    private fun DSLContext.getDescriptionImageSubQuery() =
        select(
            EBOOK_IMAGE.EBOOK_ID,
            DSL.jsonArrayAgg(
                DSL.jsonObject(
                    key("id").value(EBOOK_IMAGE.EBOOK_IMAGE_ID),
                    key("image_path").value(EBOOK_IMAGE.IMAGE_PATH),
                    key("order").value(EBOOK_IMAGE.IMAGE_ORDER),
                )
            ).`as`("description_image_json_data"),
        ).from(
            EBOOK_IMAGE
        ).where(
            EBOOK_IMAGE.IMAGE_TYPE.eq(EbookImageType.DESCRIPTION.name)
        ).groupBy(
            EBOOK_IMAGE.EBOOK_ID
        ).asTable("ebook_description_image")

    private fun DSLContext.getReviewSubQuery() =
        select(
            REVIEW.EBOOK_ID,
            DSL.avg(REVIEW.RATING).`as`("review_rating"),
            DSL.count(REVIEW.REVIEW_ID).`as`("review_count"),
        ).from(
            REVIEW
        ).groupBy(
            REVIEW.EBOOK_ID
        ).asTable("review")

    private fun DSLContext.getRelatedCategorySubQuery() =
        select(
            RELATED_CATEGORY.EBOOK_ID,
            DSL.arrayAgg(RELATED_CATEGORY.CATEGORY_ID).`as`("related_category_id_list"),
        ).from(
            RELATED_CATEGORY
        ).groupBy(
            RELATED_CATEGORY.EBOOK_ID
        ).asTable("related_category")

    private fun DSLContext.getMainImageSubQuery() =
        select(
            EBOOK_IMAGE.EBOOK_ID,
            DSL.jsonObject(
                key("id").value(EBOOK_IMAGE.EBOOK_IMAGE_ID),
                key("image_path").value(EBOOK_IMAGE.IMAGE_PATH),
                key("order").value(EBOOK_IMAGE.IMAGE_ORDER),
            ).`as`("main_image_json_data"),
        ).from(
            EBOOK_IMAGE
        ).where(
            EBOOK_IMAGE.IMAGE_TYPE.eq(EbookImageType.MAIN.name)
        ).asTable("ebook_main_image")

    private fun buildConditionsToGetEbooks(
        title: String? = null,
        sellerMemberId: UUID? = null,
        ebookIdList: List<UUID>? = null,
        categoryIdList: List<UUID>? = null,
    ): List<Condition> {
        val conditions = mutableListOf<Condition>()

        conditions.add(EBOOK.DELETED_DATE.isNull)

        title?.also {
            conditions.add(EBOOK.TITLE.likeIgnoreCase(it))
        }

        sellerMemberId?.also {
            conditions.add(EBOOK.SELLING_MEMBER_ID.eq(it))
        }

        ebookIdList?.also {
            conditions.add(EBOOK.EBOOK_ID.`in`(it))
        }

        categoryIdList?.also {
            conditions.add(
                field("related_category_id_list", SQLDataType.UUID.arrayDataType)
                    .contains(it.toTypedArray())
            )
        }

        return conditions
    }
}
