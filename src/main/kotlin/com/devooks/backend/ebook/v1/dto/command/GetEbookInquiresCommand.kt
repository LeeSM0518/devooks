package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.dto.Paging
import java.util.*
import org.springframework.data.domain.Pageable

class GetEbookInquiresCommand(
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

    val pageable: Pageable
        get() = paging.value
}
