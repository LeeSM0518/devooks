package com.devooks.backend.ebook.v1.entity

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "ebook_inquiry")
data class EbookInquiryEntity(
    @Id
    @Column(value = "ebook_inquiry_id")
    @get:JvmName("ebookInquiryId")
    val id: UUID? = null,
    val content: String,
    val ebookId: UUID,
    val writerMemberId: UUID,
    val writtenDate: Instant = Instant.now(),
    val modifiedDate: Instant = writtenDate,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        EbookInquiry(
            id = this.id!!,
            content = this.content,
            ebookId = this.ebookId,
            writerMemberId = this.writerMemberId,
            writtenDate = this.writtenDate,
            modifiedDate = this.modifiedDate,
        )
}
