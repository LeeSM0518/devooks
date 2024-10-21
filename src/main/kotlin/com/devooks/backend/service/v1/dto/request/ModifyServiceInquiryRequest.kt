package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import com.devooks.backend.service.v1.error.ServiceInquiryError
import com.devooks.backend.service.v1.error.validateServiceInquiryContent
import com.devooks.backend.service.v1.error.validateServiceInquiryId
import com.devooks.backend.service.v1.error.validateServiceInquiryImageIdList
import com.devooks.backend.service.v1.error.validateServiceInquiryTitle
import java.util.*

data class ModifyServiceInquiryRequest(
    val serviceInquiry: ServiceInquiry?,
    val isChanged: IsChanged?,
) {

    data class ServiceInquiry(
        val title: String? = null,
        val content: String? = null,
        val imageIdList: List<String>? = null,
    )

    data class IsChanged(
        val title: Boolean? = null,
        val content: Boolean? = null,
        val imageIdList: Boolean? = null,
    )

    fun toCommand(serviceInquiryId: String, requesterId: UUID) =
        if (isChanged != null) {
            if (serviceInquiry != null) {
                ModifyServiceInquiryCommand(
                    serviceInquiryId.validateServiceInquiryId(),
                    if (isChanged.title == true) serviceInquiry.title.validateServiceInquiryTitle() else null,
                    if (isChanged.content == true) serviceInquiry.content.validateServiceInquiryContent() else null,
                    if (isChanged.imageIdList != null) serviceInquiry.imageIdList?.validateServiceInquiryImageIdList() else null,
                    requesterId

                )
            } else {
                throw ServiceInquiryError.REQUIRED_IS_CHANGED_FOR_MODIFY.exception
            }
        } else {
            throw ServiceInquiryError.REQUIRED_SERVICE_INQUIRY_FOR_MODIFY.exception
        }

}
