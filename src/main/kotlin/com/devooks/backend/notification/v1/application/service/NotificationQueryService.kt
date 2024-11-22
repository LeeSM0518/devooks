package com.devooks.backend.notification.v1.application.service

import com.devooks.backend.notification.v1.adapter.`in`.dto.GetNotificationsRequest
import com.devooks.backend.notification.v1.adapter.`in`.dto.NotificationResponse
import com.devooks.backend.notification.v1.application.port.`in`.GetNotificationUseCase
import com.devooks.backend.notification.v1.application.port.out.LoadNotificationPort
import java.util.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class NotificationQueryService(
    private val loadNotificationPort: LoadNotificationPort,
) : GetNotificationUseCase {

    override suspend fun getCountOfUnchecked(memberId: UUID): Int =
        loadNotificationPort.loadCountOfUnchecked(memberId).toInt()

    override suspend fun getNotifications(request: GetNotificationsRequest): Page<NotificationResponse> {
        val notifications = loadNotificationPort.loadNotifications(request).map { it.toResponse() }
        val count = loadNotificationPort.loadCount(request.memberId)
        return PageImpl(notifications.toList(), request.pageable, count)
    }
}
