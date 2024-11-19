package com.devooks.backend.ebook.v1.repository.row

import com.devooks.backend.ebook.v1.dto.EbookImageDto
import com.devooks.backend.ebook.v1.dto.EbookImageDto.Companion.toEbookImageDto
import java.time.Instant
import java.util.*

data class EbookRow(
    val ebookId: UUID,
    private val mainImageJsonData: LinkedHashMap<String, Any>,
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
) {
    val mainImage: EbookImageDto = mainImageJsonData.toEbookImageDto()
}
