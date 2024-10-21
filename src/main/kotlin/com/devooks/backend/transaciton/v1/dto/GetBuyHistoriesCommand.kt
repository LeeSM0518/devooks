package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.common.dto.Paging
import java.util.*

class GetBuyHistoriesCommand(
    val ebookTitle: String?,
    val requesterId: UUID,
    private val paging: Paging,
) {
    constructor(
        ebookTitle: String,
        page: String,
        count: String,
        requesterId: UUID,
    ) : this(
        ebookTitle = ebookTitle.takeIf { it.isNotBlank() }?.let { "%$ebookTitle%" },
        requesterId = requesterId,
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

}
