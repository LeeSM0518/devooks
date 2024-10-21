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
import java.util.*

data class ModifyEbookRequest(
    val ebook: Ebook?,
    val isChanged: IsChanged?,
) {
    data class Ebook(
        val title: String? = null,
        val relatedCategoryNameList: List<String>? = null,
        val mainImageId: String? = null,
        val descriptionImageIdList: List<String>? = null,
        val introduction: String? = null,
        val tableOfContents: String? = null,
        val price: Int? = null,
    )

    data class IsChanged(
        val title: Boolean? = false,
        val relatedCategoryNameList: Boolean? = false,
        val mainImage: Boolean? = false,
        val descriptionImageList: Boolean? = false,
        val introduction: Boolean? = false,
        val tableOfContents: Boolean? = false,
        val price: Boolean? = false,
    )

    fun toCommand(ebookId: String, requesterId: UUID): ModifyEbookCommand =
        if (isChanged != null) {
            if (ebook != null) {
                ModifyEbookCommand(
                    ebookId.validateEbookId(),
                    if (isChanged.title == true) ebook.title.validateEbookTitle() else null,
                    if (isChanged.relatedCategoryNameList == true) ebook.relatedCategoryNameList.validateRelatedCategoryList() else null,
                    if (isChanged.mainImage == true) ebook.mainImageId.validateMainImageId() else null,
                    if (isChanged.descriptionImageList == true) ebook.descriptionImageIdList.validateDescriptionImageIdList() else null,
                    if (isChanged.introduction == true) ebook.introduction.validateEbookIntroduction() else null,
                    if (isChanged.tableOfContents == true) ebook.tableOfContents.validateTableOfContents() else null,
                    if (isChanged.price == true) ebook.price.validateEbookPrice() else null,
                    requesterId
                )
            } else {
                throw EbookError.REQUIRED_IS_CHANGED_FOR_MODIFY.exception
            }
        } else {
            throw EbookError.REQUIRED_EBOOK_FOR_MODIFY.exception
        }

}
