package com.devooks.backend.member.v1.dto

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.dto.CategoryDto
import com.devooks.backend.category.v1.dto.CategoryDto.Companion.toDto
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.MemberInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class GetProfileResponse(
    @Schema(description = "회원 식별자")
    val memberId: UUID,
    @Schema(description = "닉네임")
    val nickname: String,
    @Schema(description = "프로필 사진 경로")
    val profileImagePath: String,
    val profile: Profile,
    val favoriteCategories: List<CategoryDto>,
) {

    constructor(
        member: Member,
        memberInfo: MemberInfo,
        categories: List<Category>,
    ) : this(
        memberId = member.id,
        nickname = member.nickname,
        profileImagePath = member.profileImagePath,
        profile = Profile(
            blogLink = memberInfo.blogLink,
            instagramLink = memberInfo.instagramLink,
            youtubeLink = memberInfo.youtubeLink,
            introduction = memberInfo.introduction
        ),
        favoriteCategories = categories.map { it.toDto() }
    )

    data class Profile(
        @Schema(description = "블로그 링크")
        val blogLink: String,
        @Schema(description = "인스타그램 링크")
        val instagramLink: String,
        @Schema(description = "유튜브 링크")
        val youtubeLink: String,
        @Schema(description = "소개글")
        val introduction: String,
    )
}
