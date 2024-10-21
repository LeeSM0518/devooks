package com.devooks.backend.service.v1.domain

import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import java.time.Instant
import java.time.Instant.now
import java.util.*

data class ServiceInquiry(
    val id: UUID,
    val title: String,
    val content: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val inquiryProcessingStatus: InquiryProcessingStatus,
    val writerMemberId: UUID,
) {
    fun modify(command: ModifyServiceInquiryCommand): ServiceInquiry {
        return copy(
            title = command.title ?: this.title,
            content = command.content ?: this.content,
            modifiedDate = now()
        )
    }
}
