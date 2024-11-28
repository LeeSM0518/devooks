package com.devooks.backend.service.v1.dto.response

import com.devooks.backend.service.v1.dto.ServiceInquiryView
import com.devooks.backend.service.v1.dto.ServiceInquiryView.Companion.toServiceInquiryView
import com.devooks.backend.service.v1.repository.row.ServiceInquiryRow

data class GetServiceInquiriesResponse(
    val serviceInquiryList: List<ServiceInquiryView>
) {
    companion object {
        fun List<ServiceInquiryRow>.toGetServiceInquiriesResponse() =
            GetServiceInquiriesResponse(map { it.toServiceInquiryView() })
    }
}
