package com.devooks.backend.member.v1.dto

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.MemberInfo
import java.util.*

data class GetProfileResponse(
    val memberId: UUID,
    val nickname: String,
    val profileImagePath: String,
    val profile: Profile,
    val favoriteCategories: List<String>,
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
        favoriteCategories = categories.map { it.name }
    )

    data class Profile(
        val blogLink: String,
        val instagramLink: String,
        val youtubeLink: String,
        val introduction: String,
    )
}
