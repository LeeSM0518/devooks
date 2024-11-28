package com.devooks.backend.review.v1.dto

import com.devooks.backend.common.dto.Paging
import java.util.*
import org.springframework.data.domain.Pageable

data class GetReviewCommentsCommand(
    val reviewId: UUID,
    private val paging: Paging,
) {
    constructor(
        reviewId: UUID,
        page: Int,
        count: Int,
    ) : this(
        reviewId = reviewId,
        paging = Paging(page, count)
    )

    val pageable: Pageable
        get() = paging.value

    val offset: Int
        get() = paging.offset

    val limit: Int
        get() = paging.limit
}
