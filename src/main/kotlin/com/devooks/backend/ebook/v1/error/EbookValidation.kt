package com.devooks.backend.ebook.v1.error

fun Int?.validateEbookPrice(): Int =
    takeIf { it != null && it in 0..9_999_999 }
        ?: throw EbookError.INVALID_EBOOK_PRICE.exception

