package com.devooks.backend.wishlist.v1.dto

import java.util.*

data class WishlistDto(
    val id: UUID,
    val memberId: UUID,
    val ebookId: UUID,
)
