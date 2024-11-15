package com.devooks.backend.wishlist.v1.error

import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.common.error.validateUUID
import com.devooks.backend.ebook.v1.error.EbookError
import java.util.*

fun String?.validateEbookId(): UUID =
    validateNotBlank(EbookError.REQUIRED_EBOOK_ID.exception)
        .validateUUID(EbookError.INVALID_EBOOK_ID.exception)

fun List<String>.validateCategoryIds(): List<UUID> =
    map { it.validateUUID(WishlistError.INVALID_CATEGORY_ID.exception) }

fun String.validateWishlistId(): UUID =
    validateUUID(WishlistError.INVALID_WISHLIST_ID.exception)
