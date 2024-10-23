package com.devooks.backend.notification.v1.application.port.out

import com.devooks.backend.notification.v1.adapter.`in`.dto.CheckNotificationsRequest

interface ModifyNotificationUseCase {
    suspend fun check(request: CheckNotificationsRequest): Int
}
