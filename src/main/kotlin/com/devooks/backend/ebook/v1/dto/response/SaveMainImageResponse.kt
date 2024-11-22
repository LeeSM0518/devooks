package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookImage
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*
import kotlin.io.path.pathString

data class SaveMainImageResponse(
    val mainImage: MainImageDto,
) {
    data class MainImageDto(
        @Schema(description = "메인 사진 식별자")
        val id: UUID,
        @Schema(description = "메인 사진 경로")
        val imagePath: String,
    )

    companion object {
        fun EbookImage.toSaveMainImageResponse() =
            SaveMainImageResponse(
                MainImageDto(
                    id = id,
                    imagePath = imagePath.pathString
                )
            )
    }
}
