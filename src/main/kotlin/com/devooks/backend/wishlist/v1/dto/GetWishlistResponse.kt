package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.wishlist.v1.domain.Wishlist

data class GetWishlistResponse(
    val wishlist: List<WishlistDto>,
) {
    companion object {
        fun List<Wishlist>.toResponse() =
            GetWishlistResponse(map { WishlistDto(it.id, it.memberId, it.ebookId) })
    }
}
