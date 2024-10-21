package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto.Companion.toDto

data class SaveDescriptionImagesResponse(
    val descriptionImageList: List<DescriptionImageDto>,
) {
    companion object {
        fun List<EbookImage>.toSaveDescriptionImagesResponse() =
            SaveDescriptionImagesResponse(map { it.toDto() })
    }
}
