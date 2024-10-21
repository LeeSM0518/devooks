package com.devooks.backend.ebook.v1.domain

import java.nio.file.Path
import java.util.*

class EbookImage(
    val id: UUID,
    val imagePath: Path,
    val order: Int,
    val uploadMemberId: UUID,
    val ebookId: UUID?,
)
