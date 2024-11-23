package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.wishlist.v1.domain.Wishlist
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class WishlistView(
    @Schema(description = "찜 식별자")
    val id: UUID,
    @Schema(description = "회원 식별자")
    val memberId: UUID,
    @Schema(description = "전자책 식별자")
    val ebookId: UUID,
) {
    companion object {
        fun Wishlist.toWishlistView() =
            WishlistView(
                id = this.id,
                memberId = this.memberId,
                ebookId = this.ebookId,
            )
    }
}
