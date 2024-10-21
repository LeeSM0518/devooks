package com.devooks.backend.ebook.v1.domain

import com.devooks.backend.ebook.v1.error.EbookError

enum class EbookOrder {
    LATEST, REVIEW;

    companion object {
        fun String.toEbookOrder(): EbookOrder =
            runCatching {
                EbookOrder.valueOf(this)
            }.getOrElse {
                throw EbookError.INVALID_EBOOK_ORDER.exception
            }
    }
}