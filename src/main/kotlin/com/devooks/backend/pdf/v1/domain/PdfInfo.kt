package com.devooks.backend.pdf.v1.domain

import java.nio.file.Path

class PdfInfo(
    val filePath: Path,
    val pageCount: Int,
)