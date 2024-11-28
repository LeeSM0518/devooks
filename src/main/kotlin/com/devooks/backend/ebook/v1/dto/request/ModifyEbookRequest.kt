package com.devooks.backend.ebook.v1.dto.request

import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.util.*

data class ModifyEbookRequest(
    @field:Size(min = 1, max = 30)
    @Schema(description = "전자책 제목", nullable = true)
    val title: String?,
    @field:Size(min = 1)
    @Schema(description = "카테고리 식별자 목록", nullable = true)
    val relatedCategoryIdList: List<UUID>?,
    @Schema(description = "메인 사진 식별자", nullable = true)
    val mainImageId: UUID?,
    @Schema(description = "설명 사진 식별자 목록", nullable = true)
    val descriptionImageIdList: List<UUID>?,
    @field:Size(min = 1)
    @Schema(description = "소개", nullable = true)
    val introduction: String?,
    @field:Size(min = 1)
    @Schema(description = "목차", nullable = true)
    val tableOfContents: String?,
    @field:Min(0)
    @field:Max(10_000_000)
    @Schema(description = "가격", nullable = true)
    val price: Int?,
) {
    fun toCommand(ebookId: UUID, requesterId: UUID): ModifyEbookCommand =
        ModifyEbookCommand(
            ebookId = ebookId,
            title = title,
            relatedCategoryIdList = relatedCategoryIdList,
            mainImageId = mainImageId,
            descriptionImageIdList = descriptionImageIdList,
            introduction = introduction,
            tableOfContents = tableOfContents,
            price = price,
            requesterId = requesterId
        )

}
