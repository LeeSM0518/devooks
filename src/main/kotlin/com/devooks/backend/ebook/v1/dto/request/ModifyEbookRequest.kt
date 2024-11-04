package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import com.devooks.backend.ebook.v1.error.EbookError
import com.devooks.backend.ebook.v1.error.validateDescriptionImageIdList
import com.devooks.backend.ebook.v1.error.validateEbookIntroduction
import com.devooks.backend.ebook.v1.error.validateEbookPrice
import com.devooks.backend.ebook.v1.error.validateEbookTitle
import com.devooks.backend.ebook.v1.error.validateMainImageId
import com.devooks.backend.ebook.v1.error.validateRelatedCategoryList
import com.devooks.backend.ebook.v1.error.validateTableOfContents
import com.devooks.backend.wishlist.v1.error.validateEbookId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ModifyEbookRequest(
    @Schema(description = "수정할 내용 (값이 존재하지 않을 경우 수정하지 않음)")
    val ebook: Ebook?,
) {
    data class Ebook(
        @Schema(description = "전자책 이름", required = false, nullable = true)
        val title: String? = null,
        @Schema(description = "카테고리 식별자 목록", required = false, nullable = true)
        val relatedCategoryIdList: List<String>? = null,
        @Schema(description = "메인 사진 식별자", required = false, nullable = true)
        val mainImageId: String? = null,
        @Schema(description = "설명 사진 식별자 목록", required = false, nullable = true)
        val descriptionImageIdList: List<String>? = null,
        @Schema(description = "소개", required = false, nullable = true)
        val introduction: String? = null,
        @Schema(description = "목차", required = false, nullable = true)
        val tableOfContents: String? = null,
        @Schema(description = "가격", required = false, nullable = true)
        val price: Int? = null,
    )

    fun toCommand(ebookId: String, requesterId: UUID): ModifyEbookCommand =
        if (ebook != null) {
            ModifyEbookCommand(
                ebookId = ebookId.validateEbookId(),
                title = ebook.title?.validateEbookTitle(),
                relatedCategoryIdList = ebook.relatedCategoryIdList?.validateRelatedCategoryList(),
                mainImageId = ebook.mainImageId?.validateMainImageId(),
                descriptionImageIdList = ebook.descriptionImageIdList?.validateDescriptionImageIdList(),
                introduction = ebook.introduction?.validateEbookIntroduction(),
                tableOfContents = ebook.tableOfContents?.validateTableOfContents(),
                price = ebook.price?.validateEbookPrice(),
                requesterId = requesterId
            )
        } else {
            throw EbookError.REQUIRED_EBOOK_FOR_MODIFY.exception
        }

}
