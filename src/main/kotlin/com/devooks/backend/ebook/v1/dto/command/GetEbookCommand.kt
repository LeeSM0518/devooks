package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.ebook.v1.domain.EbookOrder
import java.util.*

class GetEbookCommand(
    val title: String?,
    val sellerMemberId: UUID?,
    val ebookIdList: List<UUID>?,
    val categoryIdList: List<UUID>?,
    val orderBy: EbookOrder,
    val requesterId: UUID?,
    private val paging: Paging,
) {

    constructor(
        title: String?,
        sellerMemberId: UUID?,
        ebookIdList: List<UUID>?,
        categoryIdList: List<UUID>?,
        orderBy: EbookOrder?,
        requesterId: UUID?,
        page: Int,
        count: Int,
    ) : this(
        title = title?.let { "%$title%" },
        sellerMemberId = sellerMemberId,
        ebookIdList = ebookIdList,
        categoryIdList = categoryIdList,
        orderBy = orderBy ?: EbookOrder.LATEST,
        requesterId = requesterId,
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

    val pageable = paging.value
}
