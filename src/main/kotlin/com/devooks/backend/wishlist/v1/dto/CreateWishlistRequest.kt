package com.devooks.backend.wishlist.v1.dto

import com.devooks.backend.wishlist.v1.error.validateEbookId
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateWishlistRequest(
    @Schema(description = "전자책 식별자", required = true)
    val ebookId: String?,
) {

    fun toCommand(requesterId: UUID): CreateWishlistCommand =
        CreateWishlistCommand(
            ebookId = ebookId.validateEbookId(),
            requesterId = requesterId
        )

}
