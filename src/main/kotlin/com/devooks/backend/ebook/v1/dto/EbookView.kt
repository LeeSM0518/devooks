package com.devooks.backend.ebook.v1.dto

import com.devooks.backend.ebook.v1.repository.row.EbookRow
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class EbookView(
    @Schema(description = "전자책 식별자")
    val id: UUID,
    @Schema(description = "메인 사진")
    val mainImage: EbookImageDto,
    @Schema(description = "제목")
    val title: String,
    @Schema(description = "가격")
    val price: Int,
    @Schema(description = "판매자 정보")
    val seller: EbookSellerView,
    @Schema(description = "리뷰 정보")
    val review: EbookReviewView,
    @Schema(description = "생성 날짜")
    val createdDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
    @Schema(description = "관련 카테고리 식별자 목록")
    val relatedCategoryIdList: List<UUID>,
    @Schema(description = "찜 식별자")
    val wishlistId: UUID?,
) {
    companion object {
        fun EbookRow.toEbookView() =
            EbookView(
                id = this.ebookId,
                mainImage = this.mainImage,
                title = this.title,
                price = this.price,
                seller = EbookSellerView(
                    id = this.sellerMemberId,
                    nickname = this.sellerNickname,
                    profileImagePath = this.sellerProfileImagePath ?: "",
                ),
                review = EbookReviewView(
                    rating = this.reviewRating,
                    count = this.reviewCount,
                ),
                createdDate = this.createdDate,
                modifiedDate = this.modifiedDate,
                relatedCategoryIdList = this.relatedCategoryIdList,
                wishlistId = this.wishlistId,
            )
    }
}
