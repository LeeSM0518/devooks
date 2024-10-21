package com.devooks.backend.ebook.v1.dto

import java.time.Instant
import java.util.*

data class EbookDetailView(
    val id: UUID,
    val mainImagePath: String,
    val descriptionImagePathList: List<DescriptionImageDto>?,
    val wishlistId: UUID?,
    val title: String,
    val review: ReviewView,
    val relatedCategoryNameList: List<String>,
    val sellingMemberId: UUID,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val pageCount: Int,
    val price: Int,
    val pdfId: UUID,
    val introduction: String,
    val tableOfContents: String,
)
