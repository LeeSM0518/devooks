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
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.jooq.Condition
import org.jooq.DSLContext
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
                buildConditionsToGetEbooks(command)
            )
        }.map { it.into(Long::class.java) }

    suspend fun findBy(command: GetEbookCommand): Flow<EbookRow> =
        query {
            val reviewSubQuery = getReviewSubQuery()
            val mainImageSubQuery = getMainImageSubQuery()
            val relatedCategorySubQuery = getRelatedCategorySubQuery()

            select(
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
                buildConditionsToGetEbooks(command)
            ).run {
                when (command.orderBy) {
                    EbookOrder.LATEST -> orderBy(EBOOK.CREATED_DATE.desc())
                    EbookOrder.REVIEW -> orderBy(DSL.field("review_rating", Double::class.java).desc())
                }
            }.offset(command.offset).limit(command.limit)
        }.map {
            it.into(EbookRow::class.java)
        }

    suspend fun findBy(command: GetDetailOfEbookCommand): EbookDetailRow? =
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

    private fun buildConditionsToGetEbooks(command: GetEbookCommand): List<Condition> {
        val conditions = mutableListOf<Condition>()

        conditions.add(EBOOK.DELETED_DATE.isNull)

        command.title?.also {
            conditions.add(EBOOK.TITLE.likeIgnoreCase(it))
        }

        command.sellingMemberId?.also {
            conditions.add(EBOOK.SELLING_MEMBER_ID.eq(it))
        }

        command.ebookIdList?.also {
            conditions.add(EBOOK.EBOOK_ID.`in`(it))
        }

        command.categoryIdList?.also {
            conditions.add(
                field("related_category_id_list", SQLDataType.UUID.arrayDataType)
                    .contains(it.toTypedArray())
            )
        }

        return conditions
    }
}
