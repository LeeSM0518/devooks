package com.devooks.backend.notification.v1.adapter.`in`.dto

import com.devooks.backend.notification.v1.domain.NotificationType
import com.devooks.backend.notification.v1.domain.event.NotificationContent
import com.devooks.backend.notification.v1.domain.event.NotificationNote
import java.time.Instant
import java.util.*

data class NotificationResponse(
    val id: UUID,
    val type: NotificationType,
    val content: NotificationContent,
    val note: NotificationNote,
    val receiverId: UUID,
    val notifiedDate: Instant,
    val checked: Boolean,
)
