package com.devooks.backend.notification.v1.adapter.`in`.event

import com.devooks.backend.common.utils.logger
import com.devooks.backend.notification.v1.adapter.`in`.dto.CreateNotificationRequest
import com.devooks.backend.notification.v1.adapter.`in`.dto.CreateNotificationRequest.Companion.toCreateNotificationRequest
import com.devooks.backend.notification.v1.application.port.`in`.CreateNotificationUseCase
import com.devooks.backend.notification.v1.domain.event.CreateNotificationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NotificationEventListener(
    private val createNotificationUseCase: CreateNotificationUseCase,
) {
    private val logger = logger()

    @EventListener
    fun consumeCreateDomainEvent(event: CreateNotificationEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val request: CreateNotificationRequest = event.toCreateNotificationRequest()
                createNotificationUseCase.create(request)
            }.onFailure {
                logger.error("알림 생성을 실패했습니다 [ event : ${event.createNote()} ]")
                logger.error(it.stackTraceToString())
            }
        }
    }
}