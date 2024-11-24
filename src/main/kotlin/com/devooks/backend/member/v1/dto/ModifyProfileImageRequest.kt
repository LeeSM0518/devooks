package com.devooks.backend.member.v1.dto

import com.devooks.backend.common.dto.ImageDto
import jakarta.validation.Valid

data class ModifyProfileImageRequest(
    @field:Valid
    val image: ImageDto
) {
    fun toCommand(): ModifyProfileImageCommand =
        ModifyProfileImageCommand(
            image = image.toDomain(PROFILE_IMAGE_INDEX)
        )

    companion object {
        private const val PROFILE_IMAGE_INDEX = 0
    }
}
