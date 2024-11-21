package com.devooks.backend.ebook.v1.repository.row

import com.devooks.backend.ebook.v1.dto.EbookImageDto
import com.devooks.backend.ebook.v1.dto.EbookImageDto.Companion.toEbookImageDto
import java.time.Instant
import java.util.*

data class EbookDetailRow(
    val ebookId: UUID,
    val title: String,
    val price: Int,
    val sellerMemberId: UUID,
    val sellerNickname: String,
    val sellerProfileImagePath: String?,
    val reviewRating: Double,
    val reviewCount: Int,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val relatedCategoryIdList: List<UUID>,
    val wishlistId: UUID?,
    val introduction: String,
    val tableOfContents: String,
    val pdfId: UUID,
    val pageCount: Int,
    private val mainImageJsonData: LinkedHashMap<String, Any>,
    private val descriptionImageJsonData: List<LinkedHashMap<String, Any>>,
) {
    val mainImage: EbookImageDto = mainImageJsonData.toEbookImageDto()

    val descriptionImageList: List<EbookImageDto> =
        descriptionImageJsonData
            .filter { it["id"] != null }
            .map { it.toEbookImageDto() }

}
