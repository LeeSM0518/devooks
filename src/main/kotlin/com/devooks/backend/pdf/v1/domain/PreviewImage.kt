package com.devooks.backend.pdf.v1.domain

import java.util.*

class PreviewImage(
    val id: UUID,
    val pdfId: UUID,
    val info: PreviewImageInfo
)
