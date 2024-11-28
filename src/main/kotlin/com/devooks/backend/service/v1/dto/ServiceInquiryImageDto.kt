package com.devooks.backend.service.v1.dto

import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*
import kotlin.io.path.pathString

data class ServiceInquiryImageDto(
    @Schema(description = "서비스 문의 사진 식별자")
    val id: UUID,
    @Schema(description = "사진 경로")
    val imagePath: String,
    @Schema(description = "사진 순서")
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
