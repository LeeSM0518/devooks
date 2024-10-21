package com.devooks.backend.ebook.v1.domain

import com.devooks.backend.ebook.v1.error.EbookError

enum class Top100 {
    DAILY, WEEKLY, MONTHLY;

    companion object {
        fun String.toTop100(): Top100 =
            runCatching {
                Top100.valueOf(this)
            }.getOrElse {
                throw EbookError.INVALID_TOP_100.exception
            }
    }
}