package com.devooks.backend.member.v1.dto

import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.error.validateImage

data class ModifyProfileImageRequest(
    val image: ImageDto?
) {
    fun toCommand(): ModifyProfileImageCommand =
        ModifyProfileImageCommand(
            image = image.validateImage(PROFILE_IMAGE_INDEX)
        )

    companion object {
        private const val PROFILE_IMAGE_INDEX = 0
    }
}
