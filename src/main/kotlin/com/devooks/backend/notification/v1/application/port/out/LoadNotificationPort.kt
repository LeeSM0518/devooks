package com.devooks.backend.notification.v1.application.port.out

import com.devooks.backend.notification.v1.adapter.`in`.dto.GetNotificationsRequest
import com.devooks.backend.notification.v1.domain.Notification
import java.util.*
import kotlinx.coroutines.flow.Flow

interface LoadNotificationPort {
    suspend fun loadCountOfUnchecked(memberId: UUID): Long
    suspend fun loadNotifications(request: GetNotificationsRequest): Flow<Notification>
    suspend fun loadCount(memberId: UUID): Long
}