package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.ebook.v1.domain.EbookImageType.MAIN
import com.devooks.backend.ebook.v1.dto.command.SaveImagesCommand
import jakarta.validation.Valid
import java.util.*

data class SaveMainImageRequest(
    @field:Valid
    val image: ImageDto,
) {

    fun toCommand(requesterId: UUID) =
        SaveImagesCommand(
            imageList = listOf(image.toDomain()),
            requesterId = requesterId,
            imageType = MAIN
        )

}
