package com.devooks.backend.pdf.v1.dto

import com.devooks.backend.pdf.v1.domain.Pdf
import com.devooks.backend.pdf.v1.domain.PreviewImage
import com.devooks.backend.pdf.v1.dto.PdfInfoDto.Companion.toDto
import com.devooks.backend.pdf.v1.dto.PreviewImageDto.Companion.toDto
import java.time.Instant
import java.util.*

data class PdfDto(
    val id: UUID,
    val uploadMemberId: UUID,
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