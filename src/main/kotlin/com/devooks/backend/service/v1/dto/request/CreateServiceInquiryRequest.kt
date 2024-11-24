package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.service.v1.dto.command.CreateServiceInquiryCommand
import com.devooks.backend.service.v1.error.validateServiceInquiryContent
import com.devooks.backend.service.v1.error.validateServiceInquiryImageIdList
import com.devooks.backend.service.v1.error.validateServiceInquiryTitle
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class CreateServiceInquiryRequest(
    @Schema(description = "제목", required = true)
    val title: String?,
    @Schema(description = "내용", required = true)
    val content: String?,
    @Schema(description = "사진 식별자 목록", required = true)
    val imageIdList: List<String>?,
) {
    fun toCommand(requesterId: UUID): CreateServiceInquiryCommand =
        CreateServiceInquiryCommand(
            title = title.validateServiceInquiryTitle(),
            content = content.validateServiceInquiryContent(),
            imageIdList = imageIdList?.validateServiceInquiryImageIdList(),
            requesterId = requesterId
        )
}
