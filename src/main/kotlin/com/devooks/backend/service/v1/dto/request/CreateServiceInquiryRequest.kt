package com.devooks.backend.service.v1.dto.request

import com.devooks.backend.service.v1.dto.command.CreateServiceInquiryCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.util.*

data class CreateServiceInquiryRequest(
    @field:NotBlank
    @Schema(description = "제목", required = true)
    val title: String,
    @field:NotBlank
    @Schema(description = "내용", required = true)
    val content: String,
    @Schema(description = "사진 식별자 목록")
    val imageIdList: List<UUID>?,
) {
    fun toCommand(requesterId: UUID): CreateServiceInquiryCommand =
        CreateServiceInquiryCommand(
            title = title,
            content = content,
            imageIdList = imageIdList,
            requesterId = requesterId
        )
}
