package com.devooks.backend.review.v1.dto

import com.devooks.backend.common.dto.Paging
import java.util.*

class GetReviewsCommand(
    val ebookId: UUID,
    private val paging: Paging,
) {
    constructor(
        ebookId: UUID,
        page: Int,
        count: Int,
    ) : this(
        ebookId = ebookId,
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

    val pageable = paging.value
}
