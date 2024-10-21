package com.devooks.backend.review.v1.dto

import com.devooks.backend.review.v1.error.validateReviewCommentId
import java.util.*

class DeleteReviewCommentCommand(
    val commentId: UUID,
    val requesterId: UUID,
) {
    constructor(
        commentId: String,
        requesterId: UUID,
    ) : this(
        commentId = commentId.validateReviewCommentId(),
        requesterId = requesterId,
    )
}
