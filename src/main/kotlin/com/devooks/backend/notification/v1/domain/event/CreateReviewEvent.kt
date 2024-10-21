package com.devooks.backend.notification.v1.domain.event

import com.devooks.backend.notification.v1.domain.NotificationType
import com.devooks.backend.notification.v1.domain.event.serializer.InstantSerializer
import com.devooks.backend.notification.v1.domain.event.serializer.UUIDSerializer
import java.time.Instant
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewEvent(
    @Serializable(with = UUIDSerializer::class)
    val reviewId: UUID,
    val reviewerName: String,
    @Serializable(with = UUIDSerializer::class)
    val ebookId: UUID,
    val ebookTitle: String,
    @Serializable(with = InstantSerializer::class)
    val writtenDate: Instant,
    @Serializable(with = UUIDSerializer::class)
    override val receiverId: UUID,
) : CreateNotificationEvent {
    override val notificationType: NotificationType = NotificationType.REVIEW
    override val content: NotificationContent = "[$reviewerName] 님이 [$ebookTitle]에 리뷰를 남겼습니다."
}
