package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.common.dto.Paging
import java.util.*

class GetWishlistCommand(
    val memberId: UUID,
    val categoryIdList: List<UUID>?,
    private val paging: Paging,
) {
    constructor(
        memberId: UUID,
        categoryIdList: List<UUID>?,
        page: Int,
        count: Int,
    ) : this(
        memberId = memberId,
        categoryIdList = categoryIdList,
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

    val pageable = paging.value
}
