package com.devooks.backend.review.v1.entity

import com.devooks.backend.review.v1.domain.ReviewComment
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "review_comment")
data class ReviewCommentEntity(
    @Id
    @Column(value = "review_comment_id")
    @get:JvmName("reviewCommentId")
    val id: UUID? = null,
    val content: String,
    val reviewId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant = Instant.now(),
    val modifiedDate: Instant = writtenDate,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        ReviewComment(
            id = this.id!!,
            content = this.content,
            reviewId = this.reviewId,
            writerMemberId = this.writerMemberId,
            writtenDate = this.writtenDate,
            modifiedDate = this.modifiedDate
        )
}
