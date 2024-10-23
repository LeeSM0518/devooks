package com.devooks.backend.notification.v1.application.port.out

import com.devooks.backend.notification.v1.domain.Notification

interface SaveNotificationPort {
    suspend fun save(notification: Notification): Notification
}
