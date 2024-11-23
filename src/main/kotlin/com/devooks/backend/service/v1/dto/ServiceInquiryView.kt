package com.devooks.backend.service.v1.dto

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.repository.row.ServiceInquiryRow
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class ServiceInquiryView(
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
    @Schema(description = "처리 상태", example = "WAITING")
    val inquiryProcessingStatus: InquiryProcessingStatus,
    @Schema(description = "작성자 식별자")
    val writerMemberId: UUID,
    @Schema(description = "서비스 문의 사진 목록")
    val imageList: List<ServiceInquiryImageDto>,
) {
    companion object {
        fun ServiceInquiryRow.toServiceInquiryView() =
            ServiceInquiryView(
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
