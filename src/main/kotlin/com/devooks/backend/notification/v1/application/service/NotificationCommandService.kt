package com.devooks.backend.notification.v1.application.service

import com.devooks.backend.notification.v1.adapter.`in`.dto.CreateNotificationRequest
import com.devooks.backend.notification.v1.application.port.`in`.CreateNotificationUseCase
import com.devooks.backend.notification.v1.application.port.out.SaveNotificationPort
import com.devooks.backend.notification.v1.domain.Notification
import com.devooks.backend.notification.v1.domain.Notification.Companion.toDomain
import org.springframework.stereotype.Service

@Service
class NotificationCommandService(
    private val saveNotificationPort: SaveNotificationPort
): CreateNotificationUseCase {

    override suspend fun create(request: CreateNotificationRequest): Notification {
        val notification: Notification = request.toDomain()
        return saveNotificationPort.save(notification)
    }
}