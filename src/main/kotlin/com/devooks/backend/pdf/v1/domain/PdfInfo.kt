package com.devooks.backend.pdf.v1.domain

import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

class PdfInfo(
    val filePath: Path,
    val pageCount: Int,
) {
    val systemFile = File(filePath.pathString.substring(1))
}
