package com.devooks.backend.ebook.v1.dto

import java.util.*

data class EbookView(
    val id: UUID,
    val mainImagePath: String,
    val title: String,
    val wishlistId: UUID?,
    val review: ReviewView,
    val writerName: String,
    val price: Int,
    val relatedCategoryNameList: List<String>,
)
