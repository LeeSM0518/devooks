package com.devooks.backend.notification.v1.error

import com.devooks.backend.common.error.validateUUID
import java.util.*

fun String?.validateNotificationId(): UUID? =
    this?.validateUUID(NotificationError.INVALID_NOTIFICATION_ID.exception)