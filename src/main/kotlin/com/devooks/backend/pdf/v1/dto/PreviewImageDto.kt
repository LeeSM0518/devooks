package com.devooks.backend.pdf.v1.dto

import com.devooks.backend.pdf.v1.domain.PreviewImage
import java.util.*
import kotlin.io.path.pathString

data class PreviewImageDto(
    val id: UUID,
    val imagePath: String,
    val previewOrder: Int,
    val pdfId: UUID,
) {
    companion object {
        fun PreviewImage.toDto() =
            PreviewImageDto(
                id = this.id,
                imagePath = this.info.imagePath.pathString,
                previewOrder = this.info.order,
                pdfId = this.pdfId
            )
    }
}