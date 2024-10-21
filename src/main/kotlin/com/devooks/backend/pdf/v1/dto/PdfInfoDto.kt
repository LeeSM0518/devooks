package com.devooks.backend.pdf.v1.dto

import com.devooks.backend.pdf.v1.domain.PdfInfo
import kotlin.io.path.pathString

data class PdfInfoDto(
    val pageCount: Int,
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