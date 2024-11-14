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
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateEbookRequest(
    @Schema(description = "PDF 식별자", required = true, nullable = false)
    val pdfId: String?,
    @Schema(description = "제목", required = true, nullable = false)
    val title: String?,
    @Schema(description = "카테고리 식별자 목록", required = true, nullable = false)
    val relatedCategoryIdList: List<String>?,
    @Schema(description = "메인 사진 식별자", required = true, nullable = false)
    val mainImageId: String?,
    @Schema(description = "설명 사진 식별자 목록", required = true, nullable = false)
    val descriptionImageIdList: List<String>?,
    @Schema(description = "가격", required = true, nullable = false)
    val price: Int?,
    @Schema(description = "소개", required = true, nullable = false)
    val introduction: String?,
    @Schema(description = "목차", required = true, nullable = false)
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
