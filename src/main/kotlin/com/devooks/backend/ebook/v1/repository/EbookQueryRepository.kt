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
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.key
import org.jooq.impl.SQLDataType
import org.springframework.stereotype.Repository

@Repository
class EbookQueryRepository : JooqR2dbcRepository() {

    suspend fun findBy(command: GetEbookCommand): Flow<EbookRow> =
        query {
            val mainImageTable = EBOOK_IMAGE.`as`("ebook_main_image")
            val relatedCategoryTable = DSL.name("related_category")

            val mainImageSubQuery =
                select(
                    *EBOOK_IMAGE.fields()
                ).from(
                    EBOOK_IMAGE
                ).where(
                    EBOOK_IMAGE.IMAGE_TYPE.eq(EbookImageType.MAIN.name)
                ).asTable(
                    mainImageTable
                )

            val relatedCategorySubQuery =
                select(
                    RELATED_CATEGORY.EBOOK_ID,
                    DSL.coalesce(
                        DSL.arrayAgg(RELATED_CATEGORY.CATEGORY_ID),
                        DSL.inline("{}")
                    ).`as`("related_category_id_list"),
                ).from(
                    RELATED_CATEGORY
                ).groupBy(
                    RELATED_CATEGORY.EBOOK_ID
                ).asTable(
                    relatedCategoryTable
                )

            select(
                EBOOK.EBOOK_ID,
                DSL.jsonObject(
                    key("id").value(mainImageSubQuery.field(EBOOK_IMAGE.EBOOK_IMAGE_ID)),
                    key("image_path").value(mainImageSubQuery.field(EBOOK_IMAGE.IMAGE_PATH)),
                    key("order").value(mainImageSubQuery.field(EBOOK_IMAGE.IMAGE_ORDER)),
                ).`as`("main_image_json_data"),
                EBOOK.TITLE,
                WISHLIST.WISHLIST_ID,
                DSL.coalesce(DSL.avg(REVIEW.RATING), 0.0).`as`("review_rating"),
                DSL.count(REVIEW.REVIEW_ID).`as`("review_count"),
                MEMBER.MEMBER_ID,
                MEMBER.NICKNAME,
                MEMBER.PROFILE_IMAGE_PATH,
                EBOOK.PRICE,
                relatedCategorySubQuery.field("related_category_id_list", SQLDataType.UUID.arrayDataType)!!
                    .`as`("related_category_id_list")
            ).from(
                EBOOK
                    .join(MEMBER).on(EBOOK.SELLING_MEMBER_ID.eq(MEMBER.MEMBER_ID))
                    .leftJoin(REVIEW).on(EBOOK.EBOOK_ID.eq(REVIEW.EBOOK_ID))
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
            ).groupBy(
                EBOOK.EBOOK_ID,
                EBOOK_IMAGE.EBOOK_IMAGE_ID,
                MEMBER.MEMBER_ID,
                field("related_category_id_list", SQLDataType.UUID.arrayDataType)
            ).run {
                when (command.orderBy) {
                    EbookOrder.LATEST -> orderBy(EBOOK.CREATED_DATE.desc())
                    EbookOrder.REVIEW -> orderBy(field("rating", Double::class.java)!!.desc())
                }
            }.offset(command.offset).limit(command.limit)
        }.map {
            it.into(EbookRow::class.java)
        }

    suspend fun findBy(command: GetDetailOfEbookCommand): EbookDetailRow? =
        query {
            val mainImageTable = EBOOK_IMAGE.`as`("ebook_main_image")
            val descriptionImageTable = EBOOK_IMAGE.`as`("ebook_description_image")
            val relatedCategoryTable = DSL.name("related_category")

            val mainImageSubQuery =
                select(
                    *EBOOK_IMAGE.fields()
                ).from(
                    EBOOK_IMAGE
                ).where(
                    EBOOK_IMAGE.IMAGE_TYPE.eq(EbookImageType.MAIN.name)
                ).asTable(
                    mainImageTable
                )

            val descriptionImageSubQuery =
                select(
                    *EBOOK_IMAGE.fields()
                ).from(
                    EBOOK_IMAGE
                ).where(
                    EBOOK_IMAGE.IMAGE_TYPE.eq(EbookImageType.DESCRIPTION.name)
                ).asTable(
                    descriptionImageTable
                )

            val relatedCategorySubQuery =
                select(
                    RELATED_CATEGORY.EBOOK_ID,
                    DSL.coalesce(
                        DSL.arrayAgg(RELATED_CATEGORY.CATEGORY_ID),
                        DSL.inline("{}")
                    ).`as`("related_category_id_list"),
                ).from(
                    RELATED_CATEGORY
                ).groupBy(
                    RELATED_CATEGORY.EBOOK_ID
                ).asTable(
                    relatedCategoryTable
                )

            select(
                EBOOK.EBOOK_ID.`as`("id"),
                EBOOK.TITLE,
                EBOOK.SELLING_MEMBER_ID,
                MEMBER.NICKNAME,
                MEMBER.PROFILE_IMAGE_PATH,
                EBOOK.CREATED_DATE,
                EBOOK.MODIFIED_DATE,
                EBOOK.PRICE,
                EBOOK.INTRODUCTION,
                EBOOK.TABLE_OF_CONTENTS,
                PDF.PDF_ID,
                PDF.PAGE_COUNT,
                DSL.coalesce(DSL.avg(REVIEW.RATING), 0.0).`as`("review_rating"),
                DSL.count(REVIEW.REVIEW_ID).`as`("review_count"),
                WISHLIST.WISHLIST_ID,
                DSL.jsonObject(
                    key("id").value(mainImageSubQuery.field(EBOOK_IMAGE.EBOOK_IMAGE_ID)),
                    key("image_path").value(mainImageSubQuery.field(EBOOK_IMAGE.IMAGE_PATH)),
                    key("order").value(mainImageSubQuery.field(EBOOK_IMAGE.IMAGE_ORDER)),
                ).`as`("main_image_json_data"),
                DSL.coalesce(
                    DSL.jsonArrayAgg(
                        DSL.jsonObject(
                            key("id").value(descriptionImageSubQuery.field(EBOOK_IMAGE.EBOOK_IMAGE_ID)),
                            key("image_path").value(descriptionImageSubQuery.field(EBOOK_IMAGE.IMAGE_PATH)),
                            key("order").value(descriptionImageSubQuery.field(EBOOK_IMAGE.IMAGE_ORDER)),
                        )
                    ),
                    DSL.inline("[]")
                ).`as`("description_image_json_data"),
                relatedCategorySubQuery.field("related_category_id_list", SQLDataType.UUID.arrayDataType)!!
                    .`as`("related_category_id_list")
            ).from(
                EBOOK
                    .join(PDF).on(EBOOK.PDF_ID.eq(PDF.PDF_ID))
                    .join(MEMBER).on(EBOOK.SELLING_MEMBER_ID.eq(MEMBER.MEMBER_ID))
                    .leftJoin(REVIEW).on(EBOOK.EBOOK_ID.eq(REVIEW.EBOOK_ID))
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
            ).groupBy(
                EBOOK.EBOOK_ID,
                WISHLIST.WISHLIST_ID,
                field("related_category_id_list", SQLDataType.UUID.arrayDataType),
                PDF.PDF_ID,
                mainImageSubQuery.field(EBOOK_IMAGE.EBOOK_IMAGE_ID),
                mainImageSubQuery.field(EBOOK_IMAGE.IMAGE_PATH),
                mainImageSubQuery.field(EBOOK_IMAGE.IMAGE_ORDER),
                MEMBER.MEMBER_ID,
                MEMBER.NICKNAME,
                MEMBER.PROFILE_IMAGE_PATH
            )
        }.map {
            it.into(EbookDetailRow::class.java)
        }.firstOrNull()

    private fun buildConditionsToGetEbooks(command: GetEbookCommand): List<Condition> {
        val conditions = mutableListOf<Condition>()

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
                DSL.field("category_id_list", SQLDataType.UUID.arrayDataType)
                    .contains(it.toTypedArray())
            )
        }

        return conditions
    }
}
