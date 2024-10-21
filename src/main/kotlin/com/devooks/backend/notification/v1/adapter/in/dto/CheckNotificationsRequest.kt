package com.devooks.backend.notification.v1.adapter.`in`.dto

import com.devooks.backend.notification.v1.error.validateNotificationId
import java.util.*

data class CheckNotificationsRequest(
    val memberId: UUID,
    val notificationId: UUID?
) {
    constructor(
        memberId: UUID,
        notificationId: String?
    ): this(
        memberId = memberId,
        notificationId = notificationId?.validateNotificationId()
    )
}
