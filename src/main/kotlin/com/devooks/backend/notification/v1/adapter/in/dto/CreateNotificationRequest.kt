package com.devooks.backend.notification.v1.adapter.`in`.dto

import com.devooks.backend.notification.v1.domain.NotificationType
import com.devooks.backend.notification.v1.domain.event.CreateNotificationEvent
import com.devooks.backend.notification.v1.domain.event.NotificationContent
import com.devooks.backend.notification.v1.domain.event.NotificationNote
import java.util.*

data class CreateNotificationRequest(
    val content: NotificationContent,
    val note: NotificationNote,
    val receiverId: UUID,
    val type: NotificationType,
) {
    companion object {
        fun CreateNotificationEvent.toCreateNotificationRequest() =
            CreateNotificationRequest(
                content = content,
                note = createNote(),
                receiverId = receiverId,
                type = notificationType
            )
    }
}
