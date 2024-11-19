package com.devooks.backend.ebook.v1.repository.row

import com.devooks.backend.ebook.v1.dto.EbookImageDto
import com.devooks.backend.ebook.v1.dto.EbookImageDto.Companion.toEbookImageDto
import java.time.Instant
import java.util.*

data class EbookDetailRow(
    val id: UUID,
    private val mainImageJsonData: LinkedHashMap<String, Any>,
    private val descriptionImageJsonData: List<LinkedHashMap<String, Any>>,
    val wishlistId: UUID?,
    val title: String,
    val introduction: String,
    val tableOfContents: String,
    val reviewRating: Double,
    val reviewCount: Int,
    val relatedCategoryIdList: List<UUID>,
    val sellingMemberId: UUID,
    val nickname: String,
    val profileImagePath: String?,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val price: Int,
    val pdfId: UUID,
    val pageCount: Int,
) {
    val mainImage: EbookImageDto = mainImageJsonData.toEbookImageDto()

    val descriptionImageList: List<EbookImageDto> =
        descriptionImageJsonData
            .filter { it["id"] != null }
            .map { it.toEbookImageDto() }

}
