package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.dto.CategoryDto
import com.devooks.backend.category.v1.dto.CategoryDto.Companion.toDto
import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto.Companion.toDto
import java.time.Instant
import java.util.*

data class EbookResponse(
    val id: UUID,
    val pdfId: UUID,
    val mainImageId: UUID,
    val relatedCategoryNameList: List<CategoryDto>,
    val title: String,
    val price: Int,
    val tableOfContents: String,
    val introduction: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val descriptionImageList: List<DescriptionImageDto>,
    val sellingMemberId: UUID,
    val deletedDate: Instant?,
) {
    constructor(
        ebook: Ebook,
        descriptionImageList: List<EbookImage>,
        categoryList: List<Category>,
    ) : this(
        id = ebook.id,
        pdfId = ebook.pdfId,
        mainImageId = ebook.mainImageId,
        relatedCategoryNameList = categoryList.map { it.toDto() },
        title = ebook.title,
        price = ebook.price,
        tableOfContents = ebook.tableOfContents,
        introduction = ebook.introduction,
        createdDate = ebook.createdDate,
        modifiedDate = ebook.modifiedDate,
        sellingMemberId = ebook.sellingMemberId,
        deletedDate = ebook.deletedDate,
        descriptionImageList = descriptionImageList.map { it.toDto() }.sortedBy { it.order }
    )
}