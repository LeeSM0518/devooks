package com.devooks.backend.wishlist.v1.dto

import java.util.*

class CreateWishlistCommand(
    val ebookId: UUID,
    val requesterId: UUID,
)
