package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.wishlist.v1.error.validateWishlistId
import java.util.*

class DeleteWishlistCommand(
    val memberId: UUID,
    val wishlistId: UUID,
) {
    constructor(
        memberId: UUID,
        wishlistId: String,
    ): this(
        memberId = memberId,
        wishlistId = wishlistId.validateWishlistId()
    )
}
