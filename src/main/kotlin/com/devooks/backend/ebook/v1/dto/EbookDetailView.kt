package com.devooks.backend.ebook.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class EbookDetailView(
    @Schema(description = "전자책 식별자")
    val id: UUID,
    @Schema(description = "메인 사진 식별자")
    val mainImagePath: String,
    @Schema(description = "설명 사진 목록")
    val descriptionImagePathList: List<DescriptionImageDto>?,
    @Schema(description = "찜 식별자")
    val wishlistId: UUID?,
    @Schema(description = "제목")
    val title: String,
    @Schema(description = "리뷰 정보")
    val review: ReviewView,
    @Schema(description = "관련 카테고리 식별자 목록")
    val relatedCategoryIdList: List<UUID>,
    @Schema(description = "작성자 이름")
    val sellingMemberId: UUID,
    @Schema(description = "생성 날짜")
    val createdDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
    @Schema(description = "페이지 개수")
    val pageCount: Int,
    @Schema(description = "가격")
    val price: Int,
    @Schema(description = "PDF 식별자")
    val pdfId: UUID,
    @Schema(description = "소개")
    val introduction: String,
    @Schema(description = "목차")
    val tableOfContents: String,
)
