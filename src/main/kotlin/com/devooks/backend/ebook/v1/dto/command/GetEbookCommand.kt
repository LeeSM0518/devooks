package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.ebook.v1.domain.EbookOrder
import com.devooks.backend.ebook.v1.domain.EbookOrder.Companion.toEbookOrder
import com.devooks.backend.ebook.v1.error.validateEbookIds
import com.devooks.backend.member.v1.error.validateMemberId
import com.devooks.backend.wishlist.v1.error.validateCategoryIds
import java.util.*

class GetEbookCommand(
    val title: String?,
    val sellingMemberId: UUID?,
    val ebookIdList: List<UUID>?,
    val categoryIdList: List<UUID>?,
    val orderBy: EbookOrder,
    val requesterId: UUID?,
    private val paging: Paging,
) {

    constructor(
        title: String,
        sellingMemberId: String,
        ebookIdList: List<String>,
        categoryIdList: List<String>,
        orderBy: String,
        requesterId: UUID?,
        page: String,
        count: String,
    ) : this(
        title = title.takeIf { it.isNotBlank() }?.let { "%$title%" },
        sellingMemberId = sellingMemberId.takeIf { it.isNotBlank() }?.let { it.validateMemberId() },
        ebookIdList = ebookIdList.takeIf { it.isNotEmpty() }?.validateEbookIds(),
        categoryIdList = categoryIdList.takeIf { it.isNotEmpty() }?.validateCategoryIds(),
        orderBy = orderBy.takeIf { it.isNotBlank() }?.toEbookOrder() ?: EbookOrder.LATEST,
        requesterId = requesterId,
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

}
