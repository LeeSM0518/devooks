package com.devooks.backend.service.v1.error

import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.common.error.validateUUID
import java.util.*

fun String?.validateServiceInquiryTitle(): String =
    validateNotBlank(ServiceInquiryError.REQUIRED_SERVICE_INQUIRY_TITLE.exception)

fun String?.validateServiceInquiryContent(): String =
    validateNotBlank(ServiceInquiryError.REQUIRED_SERVICE_INQUIRY_CONTENT.exception)

fun List<String>.validateServiceInquiryImageIdList(): List<UUID> =
    map { it.validateUUID(ServiceInquiryError.INVALID_SERVICE_INQUIRY_IMAGE_ID.exception) }

fun String?.validateServiceInquiryId(): UUID =
    validateNotBlank(ServiceInquiryError.REQUIRED_SERVICE_INQUIRY_ID.exception)
        .validateUUID(ServiceInquiryError.INVALID_SERVICE_INQUIRY_ID.exception)