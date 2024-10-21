package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.error.validateImages
import com.devooks.backend.service.v1.dto.command.SaveServiceInquiryImagesCommand
import java.util.*

data class SaveServiceInquiryImagesRequest(
    val imageList: List<ImageDto>?,
) {
    fun toCommand(requesterId: UUID) =
        SaveServiceInquiryImagesCommand(
            imageList = imageList.validateImages(),
            requesterId = requesterId
        )
}
