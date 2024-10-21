package com.devooks.backend.review.v1.repository

import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.GetReviewsCommand
import io.r2dbc.spi.Readable
import java.math.BigInteger
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class ReviewQueryRepository(
    private val databaseClient: DatabaseClient,
) {
    suspend fun findBy(command: GetReviewsCommand): List<Review> {
        val binding = mutableMapOf<String, Any>()
        val query = """
            SELECT r.*
            FROM review r, ebook e
            WHERE r.ebook_id = e.ebook_id 
            ${
            command.ebookId?.let {
                binding["ebookId"] = it
                "AND r.ebook_id = :ebookId"
            } ?: ""
        } 
            ${
            command.memberId?.let {
                binding["memberId"] = it
                "AND e.selling_member_id = :memberId"
            } ?: ""
        }
            ORDER BY r.written_date DESC
            OFFSET ${command.offset} LIMIT ${command.limit};
        """.trimIndent()

        return databaseClient
            .sql(query)
            .bindValues(binding)
            .map { row -> mapToDomain(row) }
            .all()
            .asFlow()
            .toList()
    }

    private fun mapToDomain(row: Readable) = Review(
        id = row.get("review_id", UUID::class.java)!!,
        rating = row.get("rating", BigInteger::class.java)!!.toInt(),
        content = row.get("content", String::class.java)!!,
        ebookId = row.get("ebook_id", UUID::class.java)!!,
        writerMemberId = row.get("writer_member_id", UUID::class.java)!!,
        writtenDate = row.get("written_date", Instant::class.java)!!,
        modifiedDate = row.get("modified_date", Instant::class.java)!!,
    )


}
