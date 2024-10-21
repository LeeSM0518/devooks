package com.devooks.backend.notification.v1.adapter.`in`.http

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.dto.PageResponse.Companion.toResponse
import com.devooks.backend.notification.v1.adapter.`in`.dto.CheckNotificationResponse
import com.devooks.backend.notification.v1.adapter.`in`.dto.CheckNotificationsRequest
import com.devooks.backend.notification.v1.adapter.`in`.dto.GetNotificationsRequest
import com.devooks.backend.notification.v1.adapter.`in`.dto.NotificationResponse
import com.devooks.backend.notification.v1.adapter.`in`.dto.StreamCountResponse
import com.devooks.backend.notification.v1.application.port.`in`.GetNotificationUseCase
import com.devooks.backend.notification.v1.application.port.out.ModifyNotificationUseCase
import java.time.Duration
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux.interval

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationRouter(
    private val tokenService: TokenService,
    private val getNotificationUseCase: GetNotificationUseCase,
    private val modifyNotificationUseCase: ModifyNotificationUseCase,
) {

    @GetMapping("/count")
    suspend fun streamCountOfUncheckedNotifications(
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): Flow<ServerSentEvent<StreamCountResponse>> =
        tokenService
            .getMemberId(Authorization(authorization))
            .let { memberId ->
                interval(streamIntervalDuration)
                    .asFlow()
                    .map { getCountOfUncheckedNotifications(memberId) }
            }

    @GetMapping
    suspend fun getNotifications(
        @RequestHeader(AUTHORIZATION)
        authorization: String,
        @RequestParam(name = "page", defaultValue = "1")
        page: String,
        @RequestParam(name = "count", defaultValue = "10")
        count: String,
    ): PageResponse<NotificationResponse> {
        val memberId = tokenService.getMemberId(Authorization(authorization))
        val request = GetNotificationsRequest(memberId, page, count)
        val notifications: Page<NotificationResponse> = getNotificationUseCase.getNotifications(request)
        return notifications.toResponse()
    }

    @PatchMapping(path = ["/{notificationId}/checked", "/checked"])
    suspend fun checkNotifications(
        @RequestHeader(AUTHORIZATION)
        authorization: String,
        @PathVariable("notificationId", required = false)
        notificationId: String?,
    ): CheckNotificationResponse {
        val memberId = tokenService.getMemberId(Authorization(authorization))
        val request = CheckNotificationsRequest(memberId, notificationId)
        val size: Int = modifyNotificationUseCase.check(request)
        return CheckNotificationResponse(size)
    }

    private suspend fun getCountOfUncheckedNotifications(memberId: UUID): ServerSentEvent<StreamCountResponse> {
        val size: Long = getNotificationUseCase.getCountOfUnchecked(memberId)
        val response = StreamCountResponse(size)
        return ServerSentEvent
            .builder<StreamCountResponse>()
            .id(memberId.toString())
            .event("streamCountOfUncheckedNotifications")
            .retry(retryDuration)
            .data(response)
            .build()
    }

    companion object {
        private val streamIntervalDuration = Duration.ofSeconds(1)
        private val retryDuration = Duration.ofSeconds(10)
    }
}