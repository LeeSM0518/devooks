package com.devooks.backend.pdf.v1.dto

import com.devooks.backend.pdf.v1.domain.PdfInfo
import io.swagger.v3.oas.annotations.media.Schema
import kotlin.io.path.pathString

data class PdfInfoDto(
    @Schema(description = "페이지 개수")
    val pageCount: Int,
    @Schema(description = "파일 경로")
    val filePath: String,
) {
    companion object {
        fun PdfInfo.toDto() =
            PdfInfoDto(
                pageCount = this.pageCount,
                filePath = this.filePath.pathString,
            )
    }
}
