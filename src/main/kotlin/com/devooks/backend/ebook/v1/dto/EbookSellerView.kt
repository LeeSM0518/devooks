package com.devooks.backend.ebook.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class EbookSellerView(
    @Schema(description = "판매자 식별자")
    val id: UUID,
    @Schema(description = "판매자 닉네임")
    val nickname: String,
    @Schema(description = "판매자 프로필 사진")
    val profileImagePath: String,
)
