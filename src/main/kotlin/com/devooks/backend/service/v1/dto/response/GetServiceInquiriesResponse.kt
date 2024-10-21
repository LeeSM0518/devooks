package com.devooks.backend.service.v1.dto.response

import com.devooks.backend.service.v1.dto.ServiceInquiryView

data class GetServiceInquiriesResponse(
    val serviceInquiryList: List<ServiceInquiryView>
) {
    companion object {
        fun List<ServiceInquiryView>.toGetServiceInquiriesResponse() =
            GetServiceInquiriesResponse(this)
    }
}
