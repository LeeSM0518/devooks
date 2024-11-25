package com.devooks.backend.pdf.v1.entity

import com.devooks.backend.pdf.v1.domain.PreviewImage
import com.devooks.backend.pdf.v1.domain.PreviewImageInfo
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "preview_image")
data class PreviewImageEntity(
    @Id
    @Column("preview_image_id")
    @get:JvmName("previewImageId")
    val id: UUID? = null,
    val imagePath: String,
    val previewOrder: Int,
    val pdfId: UUID,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain(): PreviewImage = PreviewImage(
        id = this.id!!,
        pdfId = this.pdfId,
        info = PreviewImageInfo(
            order = previewOrder,
            imagePath = Path(imagePath)
        )
    )

    companion object {
        fun PreviewImageInfo.toEntity(pdfId: UUID) = PreviewImageEntity(
            imagePath = this.imagePath.absolutePathString(),
            previewOrder = this.order,
            pdfId = pdfId
        )
    }
}
