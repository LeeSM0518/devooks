package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.common.dto.Paging
import java.util.*

class GetSellHistoriesCommand(
    val requesterId: UUID,
    private val paging: Paging,
) {
    constructor(
        page: String,
        count: String,
        requesterId: UUID,
    ) : this(
        requesterId = requesterId,
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit
}