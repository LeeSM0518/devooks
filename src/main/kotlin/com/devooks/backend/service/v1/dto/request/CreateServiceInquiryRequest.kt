package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.service.v1.dto.command.CreateServiceInquiryCommand
import com.devooks.backend.service.v1.error.validateServiceInquiryContent
import com.devooks.backend.service.v1.error.validateServiceInquiryImageIdList
import com.devooks.backend.service.v1.error.validateServiceInquiryTitle
import java.util.*

data class CreateServiceInquiryRequest(
    val title: String?,
    val content: String?,
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
