package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.CreateEbookCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.*

data class CreateEbookRequest(
    @Schema(description = "PDF 식별자", required = true, implementation = UUID::class)
    val pdfId: UUID,
    @field:Size(min = 1, max = 30)
    @Schema(description = "전자책 제목", required = true)
    val title: String,
    @field:NotEmpty
    @Schema(description = "카테고리 식별자 목록", required = true)
    val relatedCategoryIdList: List<UUID>,
    @Schema(description = "메인 사진 식별자", required = true, implementation = UUID::class)
    val mainImageId: UUID,
    @Schema(description = "설명 사진 식별자 목록", required = true)
    val descriptionImageIdList: List<UUID>,
    @field:Min(0)
    @field:Max(10_000_000)
    @Schema(description = "가격", required = true)
    val price: Int,
    @field:NotBlank
    @Schema(description = "소개", required = true)
    val introduction: String,
    @field:NotBlank
    @Schema(description = "목차", required = true)
    val tableOfContents: String,
) {
    fun toCommand(requesterId: UUID): CreateEbookCommand =
        CreateEbookCommand(
            pdfId = pdfId,
            title = title,
            relatedCategoryIdList = relatedCategoryIdList,
            mainImageId = mainImageId,
            descriptionImageIdList = descriptionImageIdList,
            price = price,
            introduction = introduction,
            tableOfContents = tableOfContents,
            sellingMemberId = requesterId
        )
}
