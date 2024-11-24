package com.devooks.backend.service.v1.repository.row

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto
import java.time.Instant
import java.util.*

data class ServiceInquiryRow(
    val id: UUID,
    val title: String,
    val content: String,
    val createdDate: Instant,
    val modifiedDate: Instant,
    val inquiryProcessingStatus: InquiryProcessingStatus,
    val writerMemberId: UUID,
    private val imageJsonData: List<LinkedHashMap<String, Any>>
) {

    val imageList: List<ServiceInquiryImageDto> =
        imageJsonData.map {
            ServiceInquiryImageDto(
                id = UUID.fromString(it["id"] as String),
                imagePath = it["image_path"] as String,
                order = (it["order"] as Long).toInt()
            )
        }.sortedBy { it.order }
}
