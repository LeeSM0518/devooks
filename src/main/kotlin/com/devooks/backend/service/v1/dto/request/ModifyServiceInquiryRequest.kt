package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import java.util.*

data class ModifyServiceInquiryRequest(
    @field:Size(min = 1)
    @Schema(description = "제목", nullable = true)
    val title: String?,
    @field:Size(min = 1)
    @Schema(description = "내용", nullable = true)
    val content: String?,
    @Schema(description = "사진 식별자 목록", nullable = true)
    val imageIdList: List<UUID>?,
) {

    fun toCommand(serviceInquiryId: UUID, requesterId: UUID) =
        ModifyServiceInquiryCommand(
            serviceInquiryId = serviceInquiryId,
            title = this.title,
            content = this.content,
            imageIdList = this.imageIdList,
            requesterId = requesterId
        )

}
