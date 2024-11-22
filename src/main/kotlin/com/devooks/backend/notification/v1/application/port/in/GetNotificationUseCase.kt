package com.devooks.backend.notification.v1.application.port.`in`

import com.devooks.backend.notification.v1.adapter.`in`.dto.GetNotificationsRequest
import com.devooks.backend.notification.v1.adapter.`in`.dto.NotificationResponse
import java.util.*
import org.springframework.data.domain.Page

interface GetNotificationUseCase {
    suspend fun getCountOfUnchecked(memberId: UUID): Int
    suspend fun getNotifications(request: GetNotificationsRequest): Page<NotificationResponse>
}
