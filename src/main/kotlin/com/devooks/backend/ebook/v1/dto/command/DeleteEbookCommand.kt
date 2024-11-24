package com.devooks.backend.ebook.v1.dto.command

import java.util.*

class DeleteEbookCommand(
    val ebookId: UUID,
    val requesterId: UUID,
)
