package com.devooks.backend.service.v1.dto.command

import java.util.*

class ModifyServiceInquiryCommand(
    val serviceInquiryId: UUID,
    val title: String?,
    val content: String?,
    val imageIdList: List<UUID>?,
    val requesterId: UUID,
) {
    val isChangedServiceInquiry = title != null || content != null
    val isChangedImageList = imageIdList != null
}
