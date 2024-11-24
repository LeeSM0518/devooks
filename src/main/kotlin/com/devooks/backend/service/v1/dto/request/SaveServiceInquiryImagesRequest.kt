package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.dto.ImageDto.Companion.toDomain
import com.devooks.backend.service.v1.dto.command.SaveServiceInquiryImagesCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import java.util.*

data class SaveServiceInquiryImagesRequest(
    @field:NotEmpty
    @field:Valid
    val imageList: List<ImageDto>,
) {
    fun toCommand(requesterId: UUID) =
        SaveServiceInquiryImagesCommand(
            imageList = imageList.toDomain(),
            requesterId = requesterId
        )
}
