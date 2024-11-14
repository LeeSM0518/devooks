package com.devooks.backend.service.v1.dto

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.repository.row.ServiceInquiryRow
import java.time.Instant
import java.util.*

data class ServiceInquiryDto(
    val id: UUID,
    val title: String,
    val content: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val inquiryProcessingStatus: InquiryProcessingStatus,
    val writerMemberId: UUID,
    val imageList: List<ServiceInquiryImageDto>
) {
    companion object {
        fun ServiceInquiryRow.toDto() =
            ServiceInquiryDto(
                id = this.id,
                title = this.title,
                content = this.content,
                createdDate = this.createdDate,
                modifiedDate = this.modifiedDate,
                inquiryProcessingStatus = this.inquiryProcessingStatus,
                writerMemberId = this.writerMemberId,
                imageList = this.imageList
            )
    }
}
