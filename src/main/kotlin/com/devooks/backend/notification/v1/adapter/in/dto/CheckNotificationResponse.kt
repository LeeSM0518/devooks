package com.devooks.backend.notification.v1.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CheckNotificationResponse(
    @Schema(description = "확인하지 않은 알림 개수")
    val countOfUncheckedNotification: Int,
)
