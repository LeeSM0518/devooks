package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookImage
import java.util.*
import kotlin.io.path.pathString

data class SaveMainImageResponse(
    val mainImage: MainImageDto,
) {
    data class MainImageDto(
        val id: UUID,
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
