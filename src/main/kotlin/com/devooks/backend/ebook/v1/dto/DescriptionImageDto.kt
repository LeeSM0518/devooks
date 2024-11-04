package com.devooks.backend.ebook.v1.dto

import com.devooks.backend.ebook.v1.domain.EbookImage
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*
import kotlin.io.path.pathString

data class DescriptionImageDto(
    @Schema(description = "설명 사진 식별자")
    val id: UUID,
    @Schema(description = "사진 경로")
    val imagePath: String,
    @Schema(description = "사진 순서")
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
