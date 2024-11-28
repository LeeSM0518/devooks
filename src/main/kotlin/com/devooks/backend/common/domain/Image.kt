package com.devooks.backend.common.domain

import com.devooks.backend.common.error.CommonError
import java.util.*

class Image(
    val base64Raw: String,
    val extension: ImageExtension,
    val byteSize: Int,
    val order: Int
) {

    fun convertDecodedImage(): ByteArray =
        runCatching {
            Base64.getDecoder().decode(base64Raw)
        }.getOrElse {
            throw CommonError.FAIL_SAVE_IMAGE.exception
        }
}
