package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.dto.EbookImageDto
import com.devooks.backend.ebook.v1.dto.EbookImageDto.Companion.toDto

data class SaveMainImageResponse(
    val mainImage: EbookImageDto,
) {
    companion object {
        fun EbookImage.toSaveMainImageResponse() =
            SaveMainImageResponse(toDto())
    }
}
