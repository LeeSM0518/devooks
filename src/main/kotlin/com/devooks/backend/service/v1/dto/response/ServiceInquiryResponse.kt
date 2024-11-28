package com.devooks.backend.service.v1.dto.response

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.domain.ServiceInquiry
import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto.Companion.toDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class ServiceInquiryResponse(
    @Schema(description = "서비스 문의 식별자")
    val id: UUID,
    @Schema(description = "제목")
    val title: String,
    @Schema(description = "내용")
    val content: String,
    @Schema(description = "생성 날짜")
    val createdDate: Instant,
    @Schema(description = "수정 날짜")
    val modifiedDate: Instant,
    @Schema(description = "처리 상태")
    val inquiryProcessingStatus: InquiryProcessingStatus,
    @Schema(description = "작성자 식별자")
    val writerMemberId: UUID,
    @Schema(description = "서비스 문의 사진 목록")
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
