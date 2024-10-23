package com.devooks.backend.notification.v1.adapter.out.persistence

import com.devooks.backend.notification.v1.domain.Notification
import com.devooks.backend.notification.v1.domain.NotificationType
import com.devooks.backend.notification.v1.domain.event.NotificationContent
import com.devooks.backend.notification.v1.domain.event.NotificationNote
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "notification")
data class NotificationEntity(
    @Id
    @Column("notification_id")
    @get:JvmName("notificationId")
    val id: UUID? = null,
    val type: NotificationType,
    val content: NotificationContent,
    val note: NotificationNote,
    val receiverId: UUID,
    val notifiedDate: Instant,
    val checked: Boolean,
) : Persistable<UUID> {
    override fun getId(): UUID? = id
    override fun isNew(): Boolean = id == null

    fun toDomain() =
        Notification(
            id = id!!,
            type = type,
            content = content,
            note = note,
            receiverId = receiverId,
            notifiedDate = notifiedDate,
            checked = checked,
        )

    companion object {
        fun Notification.toEntity() =
            NotificationEntity(
                id = this.id,
                type = this.type,
                content = this.content,
                note = this.note,
                receiverId = this.receiverId,
                notifiedDate = this.notifiedDate,
                checked = this.checked,
            )
    }
}
