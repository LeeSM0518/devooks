package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*

data class CreateWishlistRequest(
    val ebookId: String?,
) {

    fun toCommand(requesterId: UUID): CreateWishlistCommand =
        CreateWishlistCommand(
            ebookId = ebookId.validateEbookId(),
            requesterId = requesterId
        )

}
