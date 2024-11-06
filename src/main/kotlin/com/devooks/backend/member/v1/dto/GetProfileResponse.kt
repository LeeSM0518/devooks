package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.domain.FavoriteCategory
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
    @Schema(description = "관심 카테고리 식별자 목록")
    val favoriteCategoryIdList: List<UUID>,
) {

    constructor(
        member: Member,
        memberInfo: MemberInfo,
        categories: List<FavoriteCategory>,
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
        favoriteCategoryIdList = categories.map { it.categoryId }
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
