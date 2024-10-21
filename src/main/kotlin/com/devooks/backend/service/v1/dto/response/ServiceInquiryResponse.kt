package com.devooks.backend.service.v1.dto.response

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.domain.ServiceInquiry
import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto.Companion.toDto
import java.time.Instant
import java.util.*

data class ServiceInquiryResponse(
    val id: UUID,
    val title: String,
    val content: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val inquiryProcessingStatus: InquiryProcessingStatus,
    val writerMemberId: UUID,
    val imageList: List<ServiceInquiryImageDto>,
) {
    constructor(
        serviceInquiry: ServiceInquiry,
        serviceInquiryImageList: List<ServiceInquiryImage>,
    ) : this(
        id = serviceInquiry.id,
        title = serviceInquiry.title,
        content = serviceInquiry.content,
        createdDate = serviceInquiry.createdDate,
        modifiedDate = serviceInquiry.modifiedDate,
        inquiryProcessingStatus = serviceInquiry.inquiryProcessingStatus,
        writerMemberId = serviceInquiry.writerMemberId,
        imageList = serviceInquiryImageList.map { it.toDto() }.sortedBy { it.order }
    )
}
