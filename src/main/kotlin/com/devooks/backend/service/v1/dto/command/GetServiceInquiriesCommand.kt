package com.devooks.backend.service.v1.dto.command

import com.devooks.backend.common.dto.Paging
import java.util.*

class GetServiceInquiriesCommand(
    val requesterId: UUID,
    private val paging: Paging,
) {
    constructor(
        page: Int,
        count: Int,
        requesterId: UUID,
    ) : this(
        requesterId = requesterId,
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

    val pageable = paging.value
}
