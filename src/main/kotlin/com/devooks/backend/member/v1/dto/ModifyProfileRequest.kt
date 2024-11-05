package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.error.validateBlogLink
import com.devooks.backend.member.v1.error.validateEmail
import com.devooks.backend.member.v1.error.validateFavoriteCategoryIdList
import com.devooks.backend.member.v1.error.validateInstagramLink
import com.devooks.backend.member.v1.error.validateIntroduction
import com.devooks.backend.member.v1.error.validatePhoneNumber
import com.devooks.backend.member.v1.error.validateYoutubeLink
import io.swagger.v3.oas.annotations.media.Schema

data class ModifyProfileRequest(
    @Schema(description = "전화번호 (ex. 010-1234-1234)", required = false, nullable = true)
    val phoneNumber: String?,
    @Schema(description = "블로그 링크", required = false, nullable = true)
    val blogLink: String?,
    @Schema(description = "인스타그램 링크", required = false, nullable = true)
    val instagramLink: String?,
    @Schema(description = "유튜브 링크", required = false, nullable = true)
    val youtubeLink: String?,
    @Schema(description = "소개글", required = false, nullable = true)
    val introduction: String?,
    @Schema(description = "관심 카테고리 식별자 목록", required = false, nullable = true)
    val favoriteCategoryIdList: List<String>?,
    @Schema(description = "이메일", required = false, nullable = true)
    val email: String?,
) {
    fun toCommand(): ModifyProfileCommand =
        ModifyProfileCommand(
            phoneNumber = phoneNumber?.validatePhoneNumber(),
            blogLink = blogLink?.validateBlogLink(),
            instagramLink = instagramLink?.validateInstagramLink(),
            youtubeLink = youtubeLink?.validateYoutubeLink(),
            introduction = introduction?.validateIntroduction(),
            favoriteCategoryIdList = favoriteCategoryIdList?.validateFavoriteCategoryIdList(),
            email = email?.validateEmail(),
        )
}
