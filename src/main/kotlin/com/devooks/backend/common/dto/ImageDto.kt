package com.devooks.backend.common.dto

import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.domain.Image.Companion.validateByteSize
import com.devooks.backend.common.domain.ImageExtension.Companion.validateImageExtension
import com.devooks.backend.common.error.CommonError
import com.devooks.backend.common.error.validateImageOrder
import com.devooks.backend.common.error.validateNotBlank
import io.swagger.v3.oas.annotations.media.Schema

data class ImageDto(
    @Schema(description = "base64 프로필 사진", required = true, nullable = false)
    val base64Raw: String?,
    @Schema(description = "확장자 (ex. JPG, PNG, JPEG)", required = true, nullable = false)
    val extension: String?,
    @Schema(description = "파일 크기 (byte, 최대 50MB)", required = true, nullable = false)
    val byteSize: Long?,
    @Schema(description = "파일 순서", required = true, nullable = false)
    val order: Int?
) {
    fun toDomain(): Image =
        Image(
            base64Raw = base64Raw.validateNotBlank(CommonError.REQUIRED_BASE64RAW.exception),
            extension = extension.validateImageExtension(),
            byteSize = byteSize.validateByteSize(),
            order = order.validateImageOrder()
        )
}
