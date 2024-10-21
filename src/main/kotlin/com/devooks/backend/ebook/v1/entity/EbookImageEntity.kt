package com.devooks.backend.ebook.v1.entity

import com.devooks.backend.ebook.v1.domain.EbookImage
import java.util.*
import kotlin.io.path.Path
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "ebook_image")
data class EbookImageEntity(
    @Id
    @Column("ebook_image_id")
    @get:JvmName("ebookImageId")
    val id: UUID? = null,
    val imagePath: String,
    val imageOrder: Int,
    val uploadMemberId: UUID,
    val ebookId: UUID? = null,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        EbookImage(
            id = id!!,
            imagePath = Path(imagePath),
            order = imageOrder,
            uploadMemberId = uploadMemberId,
            ebookId = ebookId
        )
}