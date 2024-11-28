package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class ModifyEbookCommand(
    val ebookId: UUID,
    val title: String?,
    val relatedCategoryIdList: List<UUID>?,
    val mainImageId: UUID?,
    val descriptionImageIdList: List<UUID>?,
    val introduction: String?,
    val tableOfContents: String?,
    val price: Int?,
    val requesterId: UUID,
) {
    val isChangedEbook: Boolean =
        title != null || mainImageId != null || introduction != null || tableOfContents != null || price != null
    val isChangedRelatedCategoryIdList: Boolean = relatedCategoryIdList != null
}
