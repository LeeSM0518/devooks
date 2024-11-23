package com.devooks.backend.review.v1.dto

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*

class GetReviewsCommand(
    val ebookId: UUID,
    private val paging: Paging,
) {
    constructor(
        ebookId: String,
        page: String,
        count: String,
    ) : this(
        ebookId = ebookId.validateEbookId(),
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit

    val pageable = paging.value
}
