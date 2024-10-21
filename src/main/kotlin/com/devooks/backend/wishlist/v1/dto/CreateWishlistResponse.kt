package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.wishlist.v1.domain.Wishlist
import java.time.Instant
import java.util.*

data class CreateWishlistResponse(
    val wishlistId: UUID,
    val memberId: UUID,
    val ebookId: UUID,
    val createdDate: Instant
) {

    constructor(
        wishlist: Wishlist,
    ): this(
        wishlistId = wishlist.id,
        memberId = wishlist.memberId,
        ebookId = wishlist.ebookId,
        createdDate = wishlist.createdDate
    )

}
