package com.devooks.backend.ebook.v1.dto

import com.devooks.backend.ebook.v1.domain.EbookImage
import java.util.*
import kotlin.io.path.pathString

data class DescriptionImageDto(
    val id: UUID,
    val imagePath: String,
    val order: Int,
) {
    companion object {
        fun EbookImage.toDto() =
            DescriptionImageDto(
                id = id,
                imagePath = imagePath.pathString,
                order = order
            )
    }
}