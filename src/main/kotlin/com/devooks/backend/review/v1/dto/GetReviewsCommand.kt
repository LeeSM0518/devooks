package com.devooks.backend.review.v1.dto

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.member.v1.error.validateMemberId
import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*

class GetReviewsCommand(
    val ebookId: UUID?,
    val memberId: UUID?,
    private val paging: Paging,
) {
    constructor(
        ebookId: String,
        memberId: String,
        page: String,
        count: String,
    ) : this(
        ebookId = ebookId.takeIf { it.isNotBlank() }?.validateEbookId(),
        memberId = memberId.takeIf { it.isNotBlank() }?.validateMemberId(),
        paging = Paging(page, count)
    )

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit
}
