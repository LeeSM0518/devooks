package com.devooks.backend.review.v1.dto

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.review.v1.error.validateReviewId
import java.util.*
import org.springframework.data.domain.Pageable

data class GetReviewCommentsCommand(
    val reviewId: UUID,
    private val paging: Paging,
) {
    constructor(
        reviewId: String,
        page: Int,
        count: Int,
    ) : this(
        reviewId = reviewId.validateReviewId(),
        paging = Paging(page, count)
    )

    val pageable: Pageable
        get() = paging.value
}
