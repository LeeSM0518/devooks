package com.devooks.backend.notification.v1.adapter.out.persistence

import com.devooks.backend.notification.v1.adapter.`in`.dto.GetNotificationsRequest
import com.devooks.backend.notification.v1.application.port.out.LoadNotificationPort
import com.devooks.backend.notification.v1.domain.Notification
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

@Component
class NotificationQueryAdapter(
    private val notificationRepository: NotificationRepository,
) : LoadNotificationPort {

    override suspend fun loadCount(memberId: UUID): Long =
        notificationRepository.countByReceiverId(memberId)

    override suspend fun loadCountOfUnchecked(memberId: UUID): Long =
        notificationRepository.countByReceiverIdAndCheckedIsFalse(memberId)

    override suspend fun loadNotifications(request: GetNotificationsRequest): Flow<Notification> =
        notificationRepository
            .findAllByReceiverIdOrderByNotifiedDateDesc(request.memberId, request.pageable)
            .map { it.toDomain() }
}
