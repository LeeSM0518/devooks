package com.devooks.backend.pdf.v1.domain

import java.time.Instant
import java.util.*

class Pdf(
    val id: UUID,
    val uploadMemberId: UUID,
    val createdDate: Instant,
    val info: PdfInfo
)