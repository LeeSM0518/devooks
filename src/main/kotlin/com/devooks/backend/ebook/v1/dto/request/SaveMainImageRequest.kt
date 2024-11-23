package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.error.validateImage
import com.devooks.backend.ebook.v1.domain.EbookImageType.MAIN
import com.devooks.backend.ebook.v1.dto.command.SaveImagesCommand
import java.util.*

data class SaveMainImageRequest(
    val image: ImageDto?,
) {

    fun toCommand(requesterId: UUID) =
        SaveImagesCommand(
            imageList = listOf(image.validateImage()),
            requesterId = requesterId,
            imageType = MAIN
        )
}
