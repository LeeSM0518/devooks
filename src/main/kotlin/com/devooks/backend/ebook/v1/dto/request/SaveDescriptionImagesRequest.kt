package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.error.validateImages
import com.devooks.backend.ebook.v1.dto.command.SaveDescriptionImagesCommand
import java.util.*

data class SaveDescriptionImagesRequest(
    val imageList: List<ImageDto>?
) {
    fun toCommand(requesterId: UUID) =
        SaveDescriptionImagesCommand(
            imageList = imageList.validateImages(),
            requesterId = requesterId
        )
}
