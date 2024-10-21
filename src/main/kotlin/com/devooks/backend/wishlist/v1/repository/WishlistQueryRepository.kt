package com.devooks.backend.wishlist.v1.repository

import com.devooks.backend.wishlist.v1.domain.Wishlist
import com.devooks.backend.wishlist.v1.dto.GetWishlistCommand
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class WishlistQueryRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findBy(command: GetWishlistCommand): List<Wishlist> {
        val bindings = mutableMapOf<String, Any>()
        val query = """
            SELECT w.*
            FROM wishlist w,
                 ebook e,
                 related_category r
            WHERE w.ebook_id = e.ebook_id
              AND e.ebook_id = r.ebook_id
              AND w.member_id = ${
            command.memberId.let {
                bindings["memberId"] = command.memberId
                ":memberId"
            }
        }
              ${
            command.categoryIds?.let {
                bindings["categoryIds"] = command.categoryIds
                "AND r.category_id IN (:categoryIds)"
            } ?: ""
        } 
              OFFSET ${command.offset} LIMIT ${command.limit};
        """.trimIndent()

        return databaseClient
            .sql(query)
            .bindValues(bindings)
            .map { row ->
                Wishlist(
                    id = row.get("wishlist_id", UUID::class.java)!!,
                    memberId = row.get("member_id", UUID::class.java)!!,
                    ebookId = row.get("ebook_id", UUID::class.java)!!,
                    createdDate = row.get("created_date", Instant::class.java)!!
                )
            }
            .all()
            .asFlow()
            .toList()
    }

}