package com.devooks.backend.ebook.v1.repository

import com.devooks.backend.ebook.v1.domain.EbookOrder
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto
import com.devooks.backend.ebook.v1.dto.EbookDetailView
import com.devooks.backend.ebook.v1.dto.EbookView
import com.devooks.backend.ebook.v1.dto.ReviewView
import com.devooks.backend.ebook.v1.dto.command.GetDetailOfEbookCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookCommand
import com.devooks.backend.ebook.v1.error.EbookError
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.r2dbc.spi.Readable
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class EbookQueryRepository(
    private val databaseClient: DatabaseClient,
) {
    private val objectMapper: ObjectMapper = ObjectMapper()

    suspend fun findBy(command: GetEbookCommand): List<EbookView> {
        val binding = mutableMapOf<String, Any>()
        binding["offset"] = command.offset
        binding["limit"] = command.limit
        val query = """
            WITH ebook_with_review AS
                     (SELECT e.ebook_id,
                             ei.image_path as main_image_path,
                             e.title,
                             e.selling_member_id,
                             e.created_date,
                             ${getWishlistId(command.requesterId, binding)} AS wishlist_id,
                             COALESCE(AVG(r.rating), 0) AS rating,
                             COUNT(r.review_id)         AS count,
                             m.nickname                 AS writer_name,
                             e.price
                      FROM ebook e
                               LEFT JOIN review r ON e.ebook_id = r.ebook_id
                               LEFT JOIN ebook_image ei ON e.ebook_id = ei.ebook_id AND ei.image_type = 'MAIN'
                               LEFT JOIN member m ON e.selling_member_id = m.member_id
                      WHERE e.deleted_date IS NULL
                      GROUP BY e.ebook_id, main_image_path, e.title, e.created_date, m.nickname, e.price, e.selling_member_id
                      ),
                 related_category_with_name AS (SELECT r.ebook_id
                                                     , ARRAY_AGG(c.category_id) AS category_id_list
                                                     , ARRAY_AGG(c.name)        AS categroy_name_list
                                                FROM related_category r
                                                   , category c
                                                WHERE c.category_id = r.category_id
                                                GROUP BY r.ebook_id)
            SELECT e.ebook_id,
                   e.main_image_path,
                   e.wishlist_id,
                   e.title,
                   e.rating,
                   e.count,
                   r.categroy_name_list AS related_category_name_list,
                   e.writer_name,
                   e.price
            FROM ebook_with_review e,
                 related_category_with_name r
            WHERE e.ebook_id = r.ebook_id
            ${
            command.title?.let {
                binding["title"] = it
                "AND e.title ILIKE :title"
            } ?: ""
        }
        ${
            command.sellingMemberId?.let {
                binding["sellingMemberId"] = it
                "AND e.selling_member_id = :sellingMemberId"
            } ?: ""
        }
        ${
            command.ebookIdList?.let {
                binding["ebookIdList"] = it
                "AND e.ebook_id in (:ebookIdList)"
            } ?: ""
        }
        ${
            command.categoryIdList?.let {
                binding["categoryIdList"] = it
                "AND r.category_id_list @> ARRAY [:categoryIdList]::uuid[]"
            } ?: ""
        }
        ${
            command.orderBy.let {
                when (it) {
                    EbookOrder.LATEST -> {
                        "ORDER BY e.created_date DESC"
                    }

                    EbookOrder.REVIEW -> {
                        "ORDER BY e.rating DESC"
                    }
                }
            }
        }
            OFFSET :offset LIMIT :limit;
        """.trimIndent()

        return databaseClient
            .sql(query)
            .bindValues(binding)
            .map { row -> mapToEbookView(row) }
            .all()
            .asFlow()
            .toList()
    }

    suspend fun findBy(command: GetDetailOfEbookCommand): EbookDetailView {
        val binding = mutableMapOf<String, Any>()
        binding["ebookId"] = command.ebookId
        val query = """
                WITH related_category_name AS (SELECT r.ebook_id, ARRAY_AGG(c.name) AS category_name_list
                                   FROM related_category r,
                                        category c
                                   WHERE r.category_id = c.category_id
                                   GROUP BY r.ebook_id),
                ebook_with_review AS (SELECT e.ebook_id,
                                      e.title,
                                      ei.image_path,
                                      e.selling_member_id,
                                      e.created_date,
                                      e.modified_date,
                                      e.price,
                                      e.introduction,
                                      e.table_of_contents,
                                      e.pdf_id,
                                      e.main_image_id,
                                      COALESCE(AVG(r.rating), 0) AS rating,
                                      COUNT(r.review_id)         AS count
                               FROM ebook e
                                        LEFT JOIN review r ON e.ebook_id = r.ebook_id
                                        LEFT JOIN ebook_image ei ON e.main_image_id = ei.ebook_image_id
                               GROUP BY e.ebook_id, ei.ebook_image_id)
                SELECT e.ebook_id,
                       e.title,
                       e.image_path as main_image_path,
                       e.selling_member_id,
                       e.created_date,
                       e.modified_date,
                       e.price,
                       e.introduction,
                       e.table_of_contents,
                       e.rating,
                       e.count,
                       r.category_name_list,
                       (SELECT ARRAY_TO_JSON(ARRAY_AGG(ROW_TO_JSON(ei.*)))
                        FROM ebook_image ei
                        WHERE e.ebook_id = :ebookId and e.main_image_id != ei.ebook_image_id)
                           AS description_image_path_list,
                       ${getWishlistId(command.requesterId, binding)} AS wishlist_id,
                       p.pdf_id,
                       p.page_count
                FROM ebook_with_review e,
                     related_category_name r,
                     pdf p
                WHERE e.ebook_id = r.ebook_id
                  AND e.pdf_id = p.pdf_id
                  AND e.ebook_id = :ebookId;
        """.trimIndent()

        return databaseClient
            .sql(query)
            .bindValues(binding)
            .map { row -> mapToEbookDetailView(row) }
            .all()
            .asFlow()
            .firstOrNull()
            ?: throw EbookError.NOT_FOUND_EBOOK.exception
    }

    private fun getWishlistId(
        requesterId: UUID?,
        binding: MutableMap<String, Any>,
    ) = requesterId?.let {
        binding["requesterId"] = it
        """
            (SELECT wishlist_id
            FROM wishlist
            WHERE wishlist.ebook_id = e.ebook_id
              AND wishlist.member_id = :requesterId)
        """.trimIndent()
    } ?: "null"

    private fun mapToEbookDetailView(row: Readable) =
        EbookDetailView(
            id = row.get("ebook_id", UUID::class.java)!!,
            mainImagePath = row.get("main_image_path", String::class.java)!!,
            title = row.get("title", String::class.java)!!,
            sellingMemberId = row.get("selling_member_id", UUID::class.java)!!,
            createdDate = row.get("created_date", Instant::class.java)!!,
            modifiedDate = row.get("modified_date", Instant::class.java)!!,
            price = row.get("price", BigInteger::class.java)!!.toInt(),
            pdfId = row.get("pdf_id", UUID::class.java)!!,
            introduction = row.get("introduction", String::class.java)!!,
            tableOfContents = row.get("table_of_contents", String::class.java)!!,
            relatedCategoryNameList = row.get("category_name_list", Array<String>::class.java)!!.toList(),
            descriptionImagePathList = row.get("description_image_path_list", String::class.java)?.let {
                val imagePathList = objectMapper.readValue<List<Map<String, String>>>(it)
                imagePathList.map { imagePath ->
                    DescriptionImageDto(
                        id = UUID.fromString(imagePath["ebook_image_id"]!!),
                        imagePath = imagePath["image_path"]!!,
                        order = imagePath["image_order"]!!.toInt(),
                    )
                }
            },
            pageCount = row.get("page_count", BigInteger::class.java)!!.toInt(),
            review = ReviewView(
                rating = row.get("rating", BigDecimal::class.java)!!.toDouble(),
                count = row.get("count", BigInteger::class.java)!!.toInt(),
            ),
            wishlistId = row.get("wishlist_id", UUID::class.java)
        )

    private fun mapToEbookView(row: Readable) =
        EbookView(
            id = row.get("ebook_id", UUID::class.java)!!,
            mainImagePath = row.get("main_image_path", String::class.java)!!,
            wishlistId = row.get("wishlist_id", UUID::class.java),
            title = row.get("title", String::class.java)!!,
            review = ReviewView(
                rating = row.get("rating", BigDecimal::class.java)!!.toDouble(),
                count = row.get("count", BigInteger::class.java)!!.toInt(),
            ),
            relatedCategoryNameList = row.get("related_category_name_list", Array<String>::class.java)!!.toList(),
            writerName = row.get("writer_name", String::class.java)!!,
            price = row.get("price", BigInteger::class.java)!!.toInt(),
        )
}
