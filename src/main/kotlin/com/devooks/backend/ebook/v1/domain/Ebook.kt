package com.devooks.backend.ebook.v1.domain

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import java.time.Instant
import java.util.*

data class Ebook(
    val id: UUID,
    val pdfId: UUID,
    val mainImageId: UUID,
    val title: String,
    val price: Int,
    val tableOfContents: String,
    val introduction: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val deletedDate: Instant?,
    val sellingMemberId: UUID,
) {
    fun modify(command: ModifyEbookCommand): Ebook {
        return copy(
            title = command.title ?: this.title,
            mainImageId = command.mainImageId ?: this.mainImageId,
            introduction = command.introduction ?: this.introduction,
            tableOfContents = command.tableOfContents ?: this.tableOfContents,
            price = command.price ?: this.price,
            modifiedDate = Instant.now()
        )
    }
}