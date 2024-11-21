package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.dto.EbookImageDto
import com.devooks.backend.ebook.v1.dto.EbookImageDto.Companion.toDto

data class SaveDescriptionImagesResponse(
    val descriptionImageList: List<EbookImageDto>,
) {
    companion object {
        fun List<EbookImage>.toSaveDescriptionImagesResponse() =
            SaveDescriptionImagesResponse(map { it.toDto() })
    }
}
