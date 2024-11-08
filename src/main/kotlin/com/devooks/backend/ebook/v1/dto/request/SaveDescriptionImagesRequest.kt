package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.error.validateImages
import com.devooks.backend.ebook.v1.domain.EbookImageType.DESCRIPTION
import com.devooks.backend.ebook.v1.dto.command.SaveImagesCommand
import java.util.*

data class SaveDescriptionImagesRequest(
    val imageList: List<ImageDto>?
) {
    fun toCommand(requesterId: UUID) =
        SaveImagesCommand(
            imageList = imageList.validateImages(),
            requesterId = requesterId,
            imageType = DESCRIPTION
        )
}
