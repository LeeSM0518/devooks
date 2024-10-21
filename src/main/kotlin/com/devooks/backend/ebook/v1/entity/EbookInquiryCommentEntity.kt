package com.devooks.backend.ebook.v1.entity

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("ebook_inquiry_comment")
data class EbookInquiryCommentEntity(
    @Id
    @Column(value = "ebook_inquiry_comment_id")
    @get:JvmName("ebookInquiryCommentId")
    val id: UUID? = null,
    val inquiryId: UUID,
    val content: String,
    val writerMemberId: UUID,
    val writtenDate: Instant = Instant.now(),
    val modifiedDate: Instant = writtenDate,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        EbookInquiryComment(
            id = this.id!!,
            inquiryId = this.inquiryId,
            content = this.content,
            writerMemberId = this.writerMemberId,
            writtenDate = this.writtenDate,
            modifiedDate = this.modifiedDate,
        )
}
