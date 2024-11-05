package com.devooks.backend.pdf.v1.dto

import com.devooks.backend.pdf.v1.domain.Pdf
import com.devooks.backend.pdf.v1.domain.PreviewImage
import com.devooks.backend.pdf.v1.dto.PdfInfoDto.Companion.toDto
import com.devooks.backend.pdf.v1.dto.PreviewImageDto.Companion.toDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class PdfDto(
    @Schema(description = "PDF 식별자")
    val id: UUID,
    @Schema(description = "회원 식별자")
    val uploadMemberId: UUID,
    @Schema(description = "생성 날짜")
    val createdDate: Instant,
    val pdfInfo: PdfInfoDto,
    val previewImageList: List<PreviewImageDto>,
) {
    companion object {
        fun Pdf.toDto(previewImageList: List<PreviewImage>) =
            PdfDto(
                id = this.id,
                uploadMemberId = this.uploadMemberId,
                createdDate = this.createdDate,
                pdfInfo = this.info.toDto(),
                previewImageList = previewImageList.map { it.toDto() }
            )
    }
}
