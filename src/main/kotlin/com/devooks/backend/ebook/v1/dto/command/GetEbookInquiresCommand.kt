package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*
import org.springframework.data.domain.Pageable

class GetEbookInquiresCommand(
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

    val pageable: Pageable
        get() = paging.value
}
