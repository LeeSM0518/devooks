package com.devooks.backend.notification.v1.adapter.`in`.http

import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.notification.v1.adapter.`in`.dto.CheckNotificationResponse
import com.devooks.backend.notification.v1.adapter.`in`.dto.NotificationResponse
import com.devooks.backend.notification.v1.adapter.`in`.dto.StreamCountResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.http.codec.ServerSentEvent

@Tag(name = "Notification", description = "알림")
interface NotificationRouterDocs {

    @Operation(summary = "확인하지 않은 알림 개수 실시간 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = TEXT_EVENT_STREAM_VALUE,
                        schema = Schema(implementation = StreamCountResponse::class)
                    )
                ]
            ),
        ]
    )
    suspend fun streamCountOfUncheckedNotifications(
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): Flow<ServerSentEvent<StreamCountResponse>>

    @Operation(summary = "알림 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK"
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- COMMON-400-1 : 페이지는 1부터 조회할 수 있습니다.\n" +
                        "- COMMON-400-2 : 개수는 1~1000 까지 조회할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun getNotifications(
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
        @Schema(description = "페이지", implementation = Int::class, required = true)
        page: Int,
        @Schema(description = "개수", implementation = Int::class, required = true)
        count: Int,
    ): PageResponse<NotificationResponse>

    @Operation(
        summary = "전체 혹은 선택된 알림 확인 여부 변경",
        description = "알림 식별자가 존재하지 않을 경우 확인되지 않을 알림을 모두 확인 상태로 변경함"
    )
    suspend fun checkNotifications(
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
        @Schema(description = "알림 식별자", required = false, nullable = true, implementation = UUID::class)
        notificationId: UUID?,
    ): CheckNotificationResponse
}
