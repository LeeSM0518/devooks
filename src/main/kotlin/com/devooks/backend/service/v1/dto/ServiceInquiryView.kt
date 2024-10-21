package com.devooks.backend.service.v1.dto

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import java.time.Instant
import java.util.*

data class ServiceInquiryView(
    val id: UUID,
    val title: String,
    val content: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val inquiryProcessingStatus: InquiryProcessingStatus,
    val writerMemberId: UUID,
    val imageList: List<ServiceInquiryImageDto>,
)
