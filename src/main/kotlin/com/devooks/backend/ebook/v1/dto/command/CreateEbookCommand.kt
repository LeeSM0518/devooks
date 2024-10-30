package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class CreateEbookCommand(
    val pdfId: UUID,
    val title: String,
    val relatedCategoryIdList: List<UUID>,
    val mainImageId: UUID,
    val descriptionImageIdList: List<UUID>,
    val price: Int,
    val introduction: String,
    val tableOfContents: String,
    val sellingMemberId: UUID
)
