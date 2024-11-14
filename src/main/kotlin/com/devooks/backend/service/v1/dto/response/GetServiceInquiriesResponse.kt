package com.devooks.backend.service.v1.dto.response

import com.devooks.backend.service.v1.dto.ServiceInquiryDto
import com.devooks.backend.service.v1.dto.ServiceInquiryDto.Companion.toDto
import com.devooks.backend.service.v1.repository.row.ServiceInquiryRow

data class GetServiceInquiriesResponse(
    val serviceInquiryList: List<ServiceInquiryDto>
) {
    companion object {
        fun List<ServiceInquiryRow>.toGetServiceInquiriesResponse() =
            GetServiceInquiriesResponse(map { it.toDto() })
    }
}
