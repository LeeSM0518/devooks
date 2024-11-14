package com.devooks.backend.pdf.v1.dto

import com.devooks.backend.pdf.v1.domain.PreviewImage
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*
import kotlin.io.path.pathString

data class PreviewImageDto(
    @Schema(description = "미리보기 식별자")
    val id: UUID,
    @Schema(description = "파일 경로")
    val imagePath: String,
    @Schema(description = "파일 순서")
    val previewOrder: Int,
    @Schema(description = "PDF 식별자")
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
