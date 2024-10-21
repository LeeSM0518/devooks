package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.ebook.v1.error.validateEbookInquiryId
import java.util.*

class DeleteEbookInquiryCommand(
    val inquiryId: UUID,
    val requesterId: UUID,
) {
    constructor(
        inquiryId: String,
        requesterId: UUID,
    ) : this(
        inquiryId = inquiryId.validateEbookInquiryId(),
        requesterId = requesterId,
    )
}
