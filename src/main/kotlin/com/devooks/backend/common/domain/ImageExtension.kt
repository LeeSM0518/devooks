package com.devooks.backend.common.domain

import com.devooks.backend.common.error.CommonError
import java.util.*

enum class ImageExtension {
    JPG, PNG, JPEG;

    override fun toString(): String =
        this.name.lowercase(Locale.getDefault())

    companion object {
        fun String?.validateImageExtension(): ImageExtension =
            runCatching {
                this?.takeIf { it.isNotBlank() }
                    ?.let { ImageExtension.valueOf(it.uppercase()) }
                    ?: throw CommonError.INVALID_IMAGE_EXTENSION.exception
            }.getOrElse {
                throw CommonError.INVALID_IMAGE_EXTENSION.exception
            }
    }
}
