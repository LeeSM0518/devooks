package com.devooks.backend.review.v1.entity

import com.devooks.backend.review.v1.domain.Review
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "review")
data class ReviewEntity(
    @Id
    @Column(value = "review_id")
    @get:JvmName("reviewId")
    val id: UUID? = null,
    val rating: Int,
    val content: String,
    val ebookId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant = Instant.now(),
    val modifiedDate: Instant = writtenDate,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        Review(
            id = this.id!!,
            rating = this.rating,
            content = this.content,
            ebookId = this.ebookId,
            writerMemberId = this.writerMemberId,
            writtenDate = this.writtenDate,
            modifiedDate = this.modifiedDate,
        )
}
