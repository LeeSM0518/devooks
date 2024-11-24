package com.devooks.backend.wishlist.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateWishlistRequest(
    @Schema(description = "전자책 식별자", implementation = UUID::class, required = true)
    val ebookId: UUID,
) {

    fun toCommand(requesterId: UUID): CreateWishlistCommand =
        CreateWishlistCommand(
            ebookId = ebookId,
            requesterId = requesterId
        )

}
