package com.devooks.backend.notification.v1.adapter.out.persistence

import com.devooks.backend.notification.v1.adapter.`in`.dto.CheckNotificationsRequest
import com.devooks.backend.notification.v1.adapter.out.persistence.NotificationEntity.Companion.toEntity
import com.devooks.backend.notification.v1.application.port.out.ModifyNotificationUseCase
import com.devooks.backend.notification.v1.application.port.out.SaveNotificationPort
import com.devooks.backend.notification.v1.domain.Notification
import com.devooks.backend.notification.v1.error.NotificationError
import java.util.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

@Component
class NotificationCommandAdapter(
    private val notificationRepository: NotificationRepository,
) : SaveNotificationPort, ModifyNotificationUseCase {

    override suspend fun save(notification: Notification): Notification {
        val entity: NotificationEntity = notification.toEntity()
        val notificationEntity = notificationRepository.save(entity)
        return notificationEntity.toDomain()
    }

    override suspend fun check(request: CheckNotificationsRequest): Int =
        getNotifications(request)
            .map { it.toDomain() }
            .map { it.check(request.memberId) }
            .map { it.toEntity() }
            .let { notificationRepository.saveAll(it).count() }

    private suspend fun getNotifications(request: CheckNotificationsRequest) =
        when (request.notificationId) {
            null -> notificationRepository.findAllByReceiverIdAndCheckedIsFalse(request.memberId)
            else -> findById(request.notificationId)
        }

    private suspend fun findById(notificationId: UUID) =
        flowOf(
            notificationRepository.findById(notificationId)
                ?: throw NotificationError.NOT_FOUND_NOTIFICATION.exception
        )
}
