package com.devooks.backend.ebook.v1.entity

import com.devooks.backend.ebook.v1.domain.Ebook
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "ebook")
data class EbookEntity(
    @Id
    @Column("ebook_id")
    @get:JvmName("ebookId")
    val id: UUID? = null,
    val sellingMemberId: UUID,
    val pdfId: UUID,
    val mainImageId: UUID,
    val title: String,
    val price: Int,
    val tableOfContents: String,
    val introduction: String,
    val createdDate: Instant = Instant.now(),
    val modifiedDate: Instant = createdDate,
    val deletedDate: Instant? = null,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        Ebook(
            id = id!!,
            sellingMemberId = sellingMemberId,
            pdfId = pdfId,
            mainImageId = mainImageId,
            title = title,
            price = price,
            tableOfContents = tableOfContents,
            introduction = introduction,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
            deletedDate = deletedDate,
        )

    companion object {
        fun Ebook.toEntity() =
            EbookEntity(
                id = id,
                sellingMemberId = sellingMemberId,
                pdfId = pdfId,
                mainImageId = mainImageId,
                title = title,
                price = price,
                tableOfContents = tableOfContents,
                introduction = introduction,
                createdDate = createdDate,
                modifiedDate = modifiedDate,
                deletedDate = deletedDate,
            )
    }

}