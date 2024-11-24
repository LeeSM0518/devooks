package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.dto.ImageDto.Companion.toDomain
import com.devooks.backend.ebook.v1.domain.EbookImageType.DESCRIPTION
import com.devooks.backend.ebook.v1.dto.command.SaveImagesCommand
import jakarta.validation.Valid
import java.util.*

data class SaveDescriptionImagesRequest(
    @field:Valid
    val imageList: List<ImageDto>
) {
    fun toCommand(requesterId: UUID) =
        SaveImagesCommand(
            imageList = imageList.toDomain(),
            requesterId = requesterId,
            imageType = DESCRIPTION
        )
}
