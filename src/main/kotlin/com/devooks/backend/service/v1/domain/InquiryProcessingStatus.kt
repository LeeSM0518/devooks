package com.devooks.backend.service.v1.domain

import com.devooks.backend.service.v1.error.ServiceInquiryError

enum class InquiryProcessingStatus {
    WAITING, PROGRESS, COMPLETED;

    companion object {
        fun String.toInquiryProcessingStatus(): InquiryProcessingStatus =
            runCatching {
                InquiryProcessingStatus.valueOf(this)
            }.getOrElse {
                throw ServiceInquiryError.FAIL_MAP_TO_INQUIRY_PROCESSING_STATUS.exception
            }
    }
}
