package com.devooks.backend.review.v1.dto

import java.util.*

class DeleteReviewCommentCommand(
    val commentId: UUID,
    val requesterId: UUID,
)
