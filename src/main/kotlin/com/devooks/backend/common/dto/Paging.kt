package com.devooks.backend.common.dto

import com.devooks.backend.common.error.CommonError
import org.springframework.data.domain.Pageable

private typealias Count = String
private typealias Page = String

data class Paging(
    val value: Pageable,
) {
    constructor(
        page: Page,
        count: Count,
    ) : this(
        value = Pageable
            .ofSize(count.toIntCount())
            .withPage(page.toIntPage() - 1)
    )

    val offset: Int = value.offset.toInt()
    val limit: Int = value.pageSize * (value.pageNumber + 1)
}

private fun Page.toIntPage() =
    runCatching {
        this.toInt().takeIf { it > 0 }
            ?: throw CommonError.INVALID_PAGE.exception
    }.getOrElse { throw CommonError.INVALID_PAGE.exception }

private fun Count.toIntCount() =
    runCatching {
        this.toInt().takeIf { it in 1..1000 }
            ?: throw CommonError.INVALID_COUNT.exception
    }.getOrElse { throw CommonError.INVALID_COUNT.exception }
