package com.devooks.backend.notification.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus

enum class NotificationError(val exception: GeneralException) {
    // 400
    INVALID_NOTIFICATION_ID(GeneralException("NOTIFICATION-400-1", HttpStatus.BAD_REQUEST, "잘못된 형식의 알림 식별자 입니다.")),

    // 403
    FORBIDDEN_MODIFY_NOTIFICATION(GeneralException("NOTIFICATION-403-1", HttpStatus.FORBIDDEN, "자신의 알림만 변경할 수 있습니다.")),

    // 404
    NOT_FOUND_NOTIFICATION(GeneralException("NOTIFICATION-404-1", HttpStatus.NOT_FOUND, "알림이 존재하지 않습니다."))
}