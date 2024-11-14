package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.wishlist.v1.domain.Wishlist
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class CreateWishlistResponse(
    @Schema(description = "찜 식별자")
    val wishlistId: UUID,
    @Schema(description = "회원 식별자")
    val memberId: UUID,
    @Schema(description = "전자책 식별자")
    val ebookId: UUID,
    @Schema(description = "생성 날짜")
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
