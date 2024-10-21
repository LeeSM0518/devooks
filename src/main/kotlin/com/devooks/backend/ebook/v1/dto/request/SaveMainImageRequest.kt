package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.domain.Image.Companion.validateByteSize
import com.devooks.backend.common.domain.ImageExtension.Companion.validateImageExtension
import com.devooks.backend.common.error.CommonError
import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.ebook.v1.dto.command.SaveMainImageCommand
import com.devooks.backend.ebook.v1.error.EbookError
import java.util.*

data class SaveMainImageRequest(
    val image: MainImageDto?,
) {

    data class MainImageDto(
        val base64Raw: String?,
        val extension: String?,
        val byteSize: Long?,
    ) {
        fun toCommand() =
            Image(
                base64Raw = base64Raw.validateNotBlank(CommonError.REQUIRED_BASE64RAW.exception),
                extension = extension.validateImageExtension(),
                byteSize = byteSize.validateByteSize(),
                order = 1
            )
    }

    fun toCommand(requesterId: UUID) =
        SaveMainImageCommand(
            image = image?.toCommand() ?: throw EbookError.REQUIRED_MAIN_IMAGE.exception,
            requesterId = requesterId
        )
}
