package com.devooks.backend.common.dto

import org.springframework.data.domain.Pageable

private typealias Count = Int
private typealias Page = Int

data class Paging(
    val value: Pageable,
) {
    constructor(
        page: Page,
        count: Count,
    ) : this(
        value = Pageable
            .ofSize(count)
            .withPage(page - 1)
    )

    val offset: Int = value.offset.toInt()
    val limit: Int = value.pageSize * (value.pageNumber + 1)
}
