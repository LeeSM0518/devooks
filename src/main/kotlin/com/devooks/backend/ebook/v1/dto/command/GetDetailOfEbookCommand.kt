package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*

class GetDetailOfEbookCommand(
    val ebookId: UUID,
    val requesterId: UUID?,
) {
    constructor(
        ebookId: String,
        requesterId: UUID?,
    ) : this(
        ebookId = ebookId.validateEbookId(),
        requesterId = requesterId
    )
}