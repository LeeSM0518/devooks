package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.dto.Paging
import com.devooks.backend.ebook.v1.error.validateEbookInquiryId
import java.util.*
import org.springframework.data.domain.Pageable

class GetEbookInquireCommentsCommand(
    val inquiryId: UUID,
    private val paging: Paging,
) {
    constructor(
        inquiryId: String,
        page: String,
        count: String,
    ) : this(
        inquiryId = inquiryId.validateEbookInquiryId(),
        paging = Paging(page, count),
    )

    val pageable: Pageable
        get() = paging.value
}
