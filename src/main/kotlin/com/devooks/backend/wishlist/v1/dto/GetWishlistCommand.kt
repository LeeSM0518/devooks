package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.wishlist.v1.error.validateCategoryIds
import java.util.*
import org.springframework.data.domain.Pageable

class GetWishlistCommand(
    val memberId: UUID,
    val categoryIds: List<UUID>?,
    private val pageable: Pageable,
) {
    constructor(
        memberId: UUID,
        categoryIds: List<String>,
        page: String,
        count: String,
    ) : this(
        memberId = memberId,
        categoryIds = categoryIds.takeIf { it.isNotEmpty() }?.validateCategoryIds(),
        pageable = Paging(page, count).value
    )

    val offset: Int
        get() = pageable.offset.toInt()

    val limit: Int
        get() = pageable.pageSize
}
