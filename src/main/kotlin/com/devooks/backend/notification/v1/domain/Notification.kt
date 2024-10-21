package com.devooks.backend.notification.v1.domain

import com.devooks.backend.notification.v1.adapter.`in`.dto.CreateNotificationRequest
import com.devooks.backend.notification.v1.adapter.`in`.dto.NotificationResponse
import com.devooks.backend.notification.v1.domain.event.NotificationContent
import com.devooks.backend.notification.v1.domain.event.NotificationNote
import com.devooks.backend.notification.v1.error.NotificationError
import java.time.Instant
import java.util.*

data class Notification(
    val id: UUID? = null,
    val type: NotificationType,
    val content: NotificationContent,
    val note: NotificationNote,
    val receiverId: UUID,
    val notifiedDate: Instant = Instant.now(),
    val checked: Boolean = false,
) {

    fun check(requesterId: UUID): Notification {
        if (receiverId != requesterId) {
            throw NotificationError.FORBIDDEN_MODIFY_NOTIFICATION.exception
        }
        return copy(checked = true)
    }

    fun toResponse() =
        NotificationResponse(
            id = id!!,
            type = type,
            content = content,
            note = note,
            receiverId = receiverId,
            notifiedDate = notifiedDate,
            checked = checked
        )

    companion object {
        fun CreateNotificationRequest.toDomain() =
            Notification(
                type = type,
                content = content,
                note = note,
                receiverId = receiverId,
            )
    }
}
