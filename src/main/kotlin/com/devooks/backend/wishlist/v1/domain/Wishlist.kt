package com.devooks.backend.wishlist.v1.domain

import java.time.Instant
import java.util.*

class Wishlist(
    val id: UUID,
    val memberId: UUID,
    val ebookId: UUID,
    val createdDate: Instant,
)
