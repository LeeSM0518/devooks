package com.devooks.backend.notification.v1.adapter.`in`.dto

import com.devooks.backend.notification.v1.domain.NotificationType
import com.devooks.backend.notification.v1.domain.event.NotificationContent
import com.devooks.backend.notification.v1.domain.event.NotificationNote
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class NotificationResponse(
    @Schema(description = "알림 식별자")
    val id: UUID,
    @Schema(
        description = "알림 유형 " +
                "(ex. REVIEW, REVIEW_COMMENT, INQUIRY, INQUIRY_COMMENT, ANNOUNCE, PURCHASE, SALES, WITHDRAWAL)",
        example = "REVIEW"
    )
    val type: NotificationType,
    @Schema(description = "내용", example = "[회원1] 님이 [전자책1]에 리뷰를 남겼습니다.")
    val content: NotificationContent,
    @Schema(
        description = "상세 정보 (알림 유형에 따라 달라질 수 있음) <br/> " +
                "- REVIEW: {\"type\":\"com.devooks.backend.notification.v1.domain.event.CreateReviewEvent\"," +
                "\"reviewId\":\"a79c4b6d-4261-4a5d-8fbb-dc8364d517cd\",\"reviewerName\":\"reviewer\"," +
                "\"ebookId\":\"66dba0ad-8f8a-413a-8249-de3e5ce5a796\",\"ebookTitle\":\"title\"," +
                "\"writtenDate\":\"2024-11-22T02:08:03.869505Z\"," +
                "\"receiverId\":\"29e4c18a-f2af-44fd-ba6f-34770dab1d55\"}",
        example = "{\"type\":\"com.devooks.backend.notification.v1.domain.event.CreateReviewEvent\"," +
                "\"reviewId\":\"a79c4b6d-4261-4a5d-8fbb-dc8364d517cd\",\"reviewerName\":\"reviewer\"," +
                "\"ebookId\":\"66dba0ad-8f8a-413a-8249-de3e5ce5a796\",\"ebookTitle\":\"title\"," +
                "\"writtenDate\":\"2024-11-22T02:08:03.869505Z\"," +
                "\"receiverId\":\"29e4c18a-f2af-44fd-ba6f-34770dab1d55\"}"
    )
    val note: NotificationNote,
    @Schema(description = "수신자 회원 식별자")
    val receiverId: UUID,
    @Schema(description = "알림 날짜")
    val notifiedDate: Instant,
    @Schema(description = "확인 여부")
    val checked: Boolean,
)
