package com.devooks.backend.service.v1.dto

import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import java.util.*
import kotlin.io.path.pathString

data class ServiceInquiryImageDto(
    val id: UUID,
    val imagePath: String,
    val order: Int,
) {
    companion object {
        fun ServiceInquiryImage.toDto() =
            ServiceInquiryImageDto(
                id = id,
                imagePath = imagePath.pathString,
                order = order
            )
    }
}
