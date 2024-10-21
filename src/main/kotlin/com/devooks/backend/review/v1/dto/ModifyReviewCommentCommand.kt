package com.devooks.backend.review.v1.dto

import java.util.*

class ModifyReviewCommentCommand(
    val content: String,
    val commentId: UUID,
    val requesterId: UUID,
)
