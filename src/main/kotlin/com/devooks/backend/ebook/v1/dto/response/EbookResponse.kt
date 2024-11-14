package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.dto.CategoryDto
import com.devooks.backend.category.v1.dto.CategoryDto.Companion.toDto
import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto.Companion.toDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class EbookResponse(
    @Schema(description = "전자책 식별자")
    val id: UUID,
    @Schema(description = "PDF 식별자")
    val pdfId: UUID,
    @Schema(description = "메인 사진 식별자")
    val mainImageId: UUID,
    @Schema(description = "관련 카테고리 목록")
    val relatedCategoryList: List<CategoryDto>,
    @Schema(description = "제목")
    val title: String,
    @Schema(description = "가격")
    val price: Int,
    @Schema(description = "목차")
    val tableOfContents: String,
    @Schema(description = "소개")
    val introduction: String,
    @Schema(description = "생성 날짜")
    val createdDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
    @Schema(description = "설명 사진 목록")
    val descriptionImageList: List<DescriptionImageDto>,
    @Schema(description = "판매자 회원 식별자")
    val sellingMemberId: UUID,
    @Schema(description = "삭제 날짜", nullable = true)
    val deletedDate: Instant?,
) {
    constructor(
        ebook: Ebook,
        descriptionImageList: List<EbookImage>,
        categoryList: List<Category>,
    ) : this(
        id = ebook.id,
        pdfId = ebook.pdfId,
        mainImageId = ebook.mainImageId,
        relatedCategoryList = categoryList.map { it.toDto() },
        title = ebook.title,
        price = ebook.price,
        tableOfContents = ebook.tableOfContents,
        introduction = ebook.introduction,
        createdDate = ebook.createdDate,
        modifiedDate = ebook.modifiedDate,
        sellingMemberId = ebook.sellingMemberId,
        deletedDate = ebook.deletedDate,
        descriptionImageList = descriptionImageList.map { it.toDto() }.sortedBy { it.order }
    )
}
