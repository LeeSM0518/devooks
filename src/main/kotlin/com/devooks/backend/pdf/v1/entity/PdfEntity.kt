package com.devooks.backend.pdf.v1.entity

import com.devooks.backend.pdf.v1.domain.Pdf
import com.devooks.backend.pdf.v1.domain.PdfInfo
import java.time.Instant
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.pathString
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "pdf")
data class PdfEntity(
    @Id
    @Column("pdf_id")
    @get:JvmName("pdfId")
    val id: UUID? = null,
    val filePath: String,
    val pageCount: Int,
    val uploadMemberId: UUID,
    val createdDate: Instant = Instant.now(),
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain(): Pdf =
        Pdf(
            id = this.id!!,
            uploadMemberId = this.uploadMemberId,
            createdDate = this.createdDate,
            info = PdfInfo(
                filePath = Path(this.filePath),
                pageCount = this.pageCount
            )
        )

    companion object {
        fun PdfInfo.toEntity(uploadMemberId: UUID): PdfEntity =
            PdfEntity(
                filePath = this.filePath.pathString,
                pageCount = this.pageCount,
                uploadMemberId = uploadMemberId
            )
    }
}