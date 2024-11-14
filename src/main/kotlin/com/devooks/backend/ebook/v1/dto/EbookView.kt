package com.devooks.backend.ebook.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class EbookView(
    @Schema(description = "전자책 식별자")
    val id: UUID,
    @Schema(description = "메인 사진 경로")
    val mainImagePath: String,
    @Schema(description = "제목")
    val title: String,
    @Schema(description = "찜 식별자")
    val wishlistId: UUID?,
    @Schema(description = "리뷰 정보")
    val review: ReviewView,
    @Schema(description = "작성자 이름")
    val writerName: String,
    @Schema(description = "가격")
    val price: Int,
    @Schema(description = "관련 카테고리 식별자 목록")
    val relatedCategoryIdList: List<UUID>,
)
