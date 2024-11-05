package com.devooks.backend.wishlist.v1.dto

import io.swagger.v3.oas.annotations.media.Schema

data class DeleteWishlistResponse(
    @Schema(description = "결과 메시지", example = "찜 삭제를 완료했습니다.")
    val message: String = "찜 삭제를 완료했습니다."
)
