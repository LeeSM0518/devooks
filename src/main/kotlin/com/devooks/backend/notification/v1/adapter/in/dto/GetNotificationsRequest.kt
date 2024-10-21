package com.devooks.backend.notification.v1.adapter.`in`.dto

import com.devooks.backend.common.dto.Paging
import java.util.*
import org.springframework.data.domain.Pageable

data class GetNotificationsRequest(
    val memberId: UUID,
    private val paging: Paging,
) {
    constructor(
        memberId: UUID,
        page: String,
        count: String,
    ) : this(memberId, Paging(page, count))

    val pageable: Pageable = paging.value
}
