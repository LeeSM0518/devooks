package com.devooks.backend.notification.v1.application.port.`in`

import com.devooks.backend.notification.v1.adapter.`in`.dto.CreateNotificationRequest
import com.devooks.backend.notification.v1.domain.Notification

interface CreateNotificationUseCase {
    suspend fun create(request: CreateNotificationRequest): Notification
}
