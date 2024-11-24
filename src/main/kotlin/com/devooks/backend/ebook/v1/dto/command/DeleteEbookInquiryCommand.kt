package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class DeleteEbookInquiryCommand(
    val inquiryId: UUID,
    val requesterId: UUID,
)
