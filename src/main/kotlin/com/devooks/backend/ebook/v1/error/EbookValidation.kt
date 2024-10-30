package com.devooks.backend.ebook.v1.error

import com.devooks.backend.category.v1.error.CategoryError
import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.common.error.validateNotEmpty
import com.devooks.backend.common.error.validateNotNull
import com.devooks.backend.common.error.validateUUID
import java.util.*

fun String?.validatePdfId(): UUID =
    validateNotNull(EbookError.REQUIRED_PDF_ID.exception)
        .validateUUID(EbookError.INVALID_PDF_ID.exception)

fun String?.validateEbookTitle(): String =
    validateNotBlank(EbookError.REQUIRED_TITLE.exception)

fun List<String>?.validateRelatedCategoryList(): List<UUID> =
    validateNotEmpty(EbookError.REQUIRED_RELATED_CATEGORY_LIST.exception)
        .map { it.validateUUID(CategoryError.INVALID_CATEGORY_ID.exception) }

fun Int?.validateEbookPrice(): Int =
    takeIf { it != null && it in 0..9_999_999 }
        ?: throw EbookError.INVALID_EBOOK_PRICE.exception

fun String?.validateEbookIntroduction(): String =
    validateNotBlank(EbookError.REQUIRED_EBOOK_INTRODUCTION.exception)

fun String?.validateTableOfContents(): String =
    validateNotBlank(EbookError.REQUIRED_TABLE_OF_CONTENTS.exception)

fun String?.validateEbookInquiryContent(): String =
    validateNotBlank(EbookError.REQUIRED_EBOOK_INQUIRY_CONTENT.exception)

fun String?.validateEbookInquiryId(): UUID =
    validateNotBlank(EbookError.REQUIRED_EBOOK_INQUIRY_ID.exception)
        .validateUUID(EbookError.INVALID_EBOOK_INQUIRY_ID.exception)

fun String?.validateEbookInquiryCommentContent(): String =
    validateNotBlank(EbookError.REQUIRED_EBOOK_INQUIRY_COMMENT_CONTENT.exception)

fun String?.validateEbookInquiryCommentId(): UUID =
    validateNotBlank(EbookError.REQUIRED_EBOOK_INQUIRY_COMMENT_ID.exception)
        .validateUUID(EbookError.INVALID_EBOOK_INQUIRY_COMMENT_ID.exception)

fun List<String>.validateEbookIds(): List<UUID> =
    map { it.validateUUID(EbookError.INVALID_EBOOK_ID.exception) }

fun String?.validateMainImageId(): UUID =
    validateNotBlank(EbookError.REQUIRED_MAIN_IMAGE_ID.exception)
        .validateUUID(EbookError.INVALID_MAIN_IMAGE_ID.exception)

fun List<String>?.validateDescriptionImageIdList(): List<UUID> =
    validateNotNull(EbookError.REQUIRED_DESCRIPTION_IMAGE_ID.exception)
        .map { it.validateUUID(EbookError.INVALID_DESCRIPTION_IMAGE_ID.exception) }
