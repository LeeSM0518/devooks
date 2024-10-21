package com.devooks.backend.common.dto

import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.domain.Image.Companion.validateByteSize
import com.devooks.backend.common.domain.ImageExtension.Companion.validateImageExtension
import com.devooks.backend.common.error.CommonError
import com.devooks.backend.common.error.validateImageOrder
import com.devooks.backend.common.error.validateNotBlank

data class ImageDto(
    val base64Raw: String?,
    val extension: String?,
    val byteSize: Long?,
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