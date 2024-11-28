package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.dto.Paging
import java.util.*
import org.springframework.data.domain.Pageable

class GetEbookInquireCommentsCommand(
    val inquiryId: UUID,
    private val paging: Paging,
) {
    constructor(
        inquiryId: UUID,
        page: Int,
        count: Int,
    ) : this(
        inquiryId = inquiryId,
        paging = Paging(page, count),
    )

    val pageable: Pageable
        get() = paging.value
}
