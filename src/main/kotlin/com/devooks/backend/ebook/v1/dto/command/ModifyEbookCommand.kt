package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class ModifyEbookCommand(
    val ebookId: UUID,
    val title: String?,
    val relatedCategoryNameList: List<String>?,
    val mainImageId: UUID?,
    val descriptionImageIdList: List<UUID>?,
    val introduction: String?,
    val tableOfContents: String?,
    val price: Int?,
    val requesterId: UUID,
) {
    val isChangedEbook: Boolean =
        title != null || mainImageId != null || introduction != null || tableOfContents != null || price != null
    val isChangedDescriptionImageList: Boolean = descriptionImageIdList != null
    val isChangedRelatedCategoryNameList: Boolean = relatedCategoryNameList != null
}
