package com.devooks.backend.service.v1.dto.response

import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto.Companion.toDto

data class SaveServiceInquiryImagesResponse(
    val imageList: List<ServiceInquiryImageDto>
) {
    companion object {
        fun List<ServiceInquiryImage>.toSaveServiceInquiryImagesResponse() =
            SaveServiceInquiryImagesResponse(map { it.toDto() })
    }
}
