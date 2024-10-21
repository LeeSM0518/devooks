package com.devooks.backend.review.v1.error

import com.devooks.backend.common.error.validateUUID
import com.devooks.backend.common.error.validateNotBlank
import java.util.*

fun String?.validateRating(): Int =
    validateNotBlank(ReviewError.REQUIRED_RATING.exception)
        .runCatching { toInt() }.getOrElse { throw ReviewError.INVALID_RATING.exception }
        .also {
            if (it !in 0..5) {
                throw ReviewError.INVALID_RATING.exception
            }
        }

fun String?.validateReviewContent(): String =
    validateNotBlank(ReviewError.REQUIRED_REVIEW_CONTENT.exception)

fun String?.validateReviewId(): UUID =
    validateNotBlank(ReviewError.REQUIRED_REVIEW_ID.exception)
        .validateUUID(ReviewError.INVALID_REVIEW_ID.exception)

fun String?.validateReviewCommentId(): UUID =
    validateNotBlank(ReviewError.REQUIRED_REVIEW_COMMENT_ID.exception)
        .validateUUID(ReviewError.INVALID_REVIEW_COMMENT_ID.exception)