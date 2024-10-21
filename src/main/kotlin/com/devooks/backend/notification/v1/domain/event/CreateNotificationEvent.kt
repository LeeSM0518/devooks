package com.devooks.backend.notification.v1.domain.event

import com.devooks.backend.notification.v1.domain.NotificationType
import java.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

typealias NotificationContent = String
typealias NotificationNote = Map<String, String>

@Serializable
sealed interface CreateNotificationEvent {
    val receiverId: UUID
    val notificationType: NotificationType
    val content: NotificationContent

    fun createNote(): NotificationNote =
        Json.decodeFromString(Json.encodeToString(kotlinx.serialization.serializer(), this))
}
