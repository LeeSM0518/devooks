package com.devooks.backend.member.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

data class ModifyProfileRequest(
    @field:Size(min = 2, max = 12)
    @Schema(description = "닉네임", nullable = true)
    val nickname: String?,
    @field:Pattern(regexp = "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{3,4}\$")
    @Schema(description = "전화번호 (ex. 010-1234-1234)", nullable = true)
    val phoneNumber: String?,
    @field:Size(min = 1, max = 255)
    @Schema(description = "블로그 링크", nullable = true)
    val blogLink: String?,
    @field:Size(min = 1, max = 255)
    @Schema(description = "인스타그램 링크", nullable = true)
    val instagramLink: String?,
    @field:Size(min = 1, max = 255)
    @Schema(description = "유튜브 링크", nullable = true)
    val youtubeLink: String?,
    @field:Size(min = 1, max = 5_000)
    @Schema(description = "소개글", nullable = true)
    val introduction: String?,
    @Schema(description = "관심 카테고리 식별자 목록", nullable = true)
    val favoriteCategoryIdList: List<UUID>?,
    @field:Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+\\.[A-Za-z]+$")
    @Schema(description = "이메일", nullable = true)
    val email: String?,
) {
    fun toCommand(): ModifyProfileCommand =
        ModifyProfileCommand(
            nickname = nickname,
            phoneNumber = phoneNumber,
            blogLink = blogLink,
            instagramLink = instagramLink,
            youtubeLink = youtubeLink,
            introduction = introduction,
            favoriteCategoryIdList = favoriteCategoryIdList,
            email = email,
        )
}
