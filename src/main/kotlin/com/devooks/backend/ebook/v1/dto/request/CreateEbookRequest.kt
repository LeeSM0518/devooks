package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookCommand
import com.devooks.backend.ebook.v1.error.validateDescriptionImageIdList
import com.devooks.backend.ebook.v1.error.validateEbookIntroduction
import com.devooks.backend.ebook.v1.error.validateEbookPrice
import com.devooks.backend.ebook.v1.error.validateEbookTitle
import com.devooks.backend.ebook.v1.error.validateMainImageId
import com.devooks.backend.ebook.v1.error.validatePdfId
import com.devooks.backend.ebook.v1.error.validateRelatedCategoryList
import com.devooks.backend.ebook.v1.error.validateTableOfContents
import java.util.*

data class CreateEbookRequest(
    val pdfId: String?,
    val title: String?,
    val relatedCategoryIdList: List<String>?,
    val mainImageId: String?,
    val descriptionImageIdList: List<String>?,
    val price: Int?,
    val introduction: String?,
    val tableOfContents: String?,
) {
    fun toCommand(requesterId: UUID): CreateEbookCommand =
        CreateEbookCommand(
            pdfId = pdfId.validatePdfId(),
            title = title.validateEbookTitle(),
            relatedCategoryIdList = relatedCategoryIdList.validateRelatedCategoryList(),
            mainImageId = mainImageId.validateMainImageId(),
            descriptionImageIdList = descriptionImageIdList.validateDescriptionImageIdList(),
            price = price.validateEbookPrice(),
            introduction = introduction.validateEbookIntroduction(),
            tableOfContents = tableOfContents.validateTableOfContents(),
            sellingMemberId = requesterId
        )
}
