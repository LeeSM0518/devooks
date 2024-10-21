package com.devooks.backend.pdf.v1.domain

import java.nio.file.Path

class PreviewImageInfo(
    val order: Int,
    val imagePath: Path,
)