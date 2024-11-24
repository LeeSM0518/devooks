package com.devooks.backend.wishlist.v1.dto

import java.util.*

class DeleteWishlistCommand(
    val memberId: UUID,
    val wishlistId: UUID,
)
