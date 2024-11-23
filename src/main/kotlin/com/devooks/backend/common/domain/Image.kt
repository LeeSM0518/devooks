package com.devooks.backend.common.domain

import com.devooks.backend.common.error.CommonError
import java.util.*

class Image(
    val base64Raw: String,
    val extension: ImageExtension,
    val byteSize: Long,
    val order: Int
) {

    fun convertDecodedImage(): ByteArray =
        runCatching {
            Base64.getDecoder().decode(base64Raw)
        }.getOrElse {
            throw CommonError.FAIL_SAVE_IMAGE.exception
        }

    companion object {
        private const val MAX_BYTE_SIZE = 50_000_000

        fun Long?.validateByteSize(): Long =
            this?.takeIf { it <= MAX_BYTE_SIZE }
                ?: throw CommonError.INVALID_BYTE_SIZE.exception
    }
}
