package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import com.devooks.backend.service.v1.error.ServiceInquiryError
import com.devooks.backend.service.v1.error.validateServiceInquiryContent
import com.devooks.backend.service.v1.error.validateServiceInquiryId
import com.devooks.backend.service.v1.error.validateServiceInquiryImageIdList
import com.devooks.backend.service.v1.error.validateServiceInquiryTitle
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ModifyServiceInquiryRequest(
    @Schema(description = "서비스 문의", required = true)
    val serviceInquiry: ServiceInquiry?,
) {

    data class ServiceInquiry(
        @Schema(description = "제목", required = false, nullable = true)
        val title: String? = null,
        @Schema(description = "내용", required = false, nullable = true)
        val content: String? = null,
        @Schema(description = "사진 식별자 목록", required = false, nullable = true)
        val imageIdList: List<String>? = null,
    )

    fun toCommand(serviceInquiryId: String, requesterId: UUID) =
        serviceInquiry
            ?.let {
                ModifyServiceInquiryCommand(
                    serviceInquiryId = serviceInquiryId.validateServiceInquiryId(),
                    title = it.title?.validateServiceInquiryTitle(),
                    content = it.content?.validateServiceInquiryContent(),
                    imageIdList = it.imageIdList?.validateServiceInquiryImageIdList(),
                    requesterId = requesterId

                )
            }
            ?: throw ServiceInquiryError.REQUIRED_SERVICE_INQUIRY_FOR_MODIFY.exception

}
