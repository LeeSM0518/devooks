package com.devooks.backend.notification.v1.domain.event

import com.devooks.backend.notification.v1.domain.NotificationType
import com.devooks.backend.notification.v1.domain.event.serializer.InstantSerializer
import com.devooks.backend.notification.v1.domain.event.serializer.UUIDSerializer
import java.time.Instant
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateEbookInquiryCommentEvent(
    @Serializable(with = UUIDSerializer::class)
    val ebookInquiryCommentId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val ebookInquiryId: UUID,
    val commenterName: String,
    @Serializable(with = UUIDSerializer::class)
    val ebookId: UUID,
    @Serializable(with = InstantSerializer::class)
    val writtenDate: Instant,
    @Serializable(with = UUIDSerializer::class)
    override val receiverId: UUID,
) : CreateNotificationEvent {
    override val notificationType: NotificationType = NotificationType.INQUIRY_COMMENT
    override val content: NotificationContent = "[$commenterName] 님이 댓글을 남겼습니다."
}
