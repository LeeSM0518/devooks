package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.wishlist.v1.error.validateCategoryIds
import java.util.*

class GetWishlistCommand(
    val memberId: UUID,
    val categoryIds: List<UUID>?,
    private val paging: Paging,
) {
    constructor(
        memberId: UUID,
        categoryIds: List<String>,
        page: Int,
        count: Int,
    ) : this(
        memberId = memberId,
        categoryIds = categoryIds.takeIf { it.isNotEmpty() }?.validateCategoryIds(),
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

    val pageable = paging.value
}
