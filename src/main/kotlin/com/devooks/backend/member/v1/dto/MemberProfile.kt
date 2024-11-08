package com.devooks.backend.member.v1.dto

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.MemberInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class MemberProfile(
    @Schema(description = "회원 식별자")
    val id: UUID,
    @Schema(description = "닉네임")
    val nickname: String,
    @Schema(description = "프로필 사진 경로")
    val profileImagePath: String,
    val favoriteCategoryList: List<Category>,
    @Schema(description = "블로그 링크")
    val blogLink: String,
    @Schema(description = "인스타그램 링크")
    val instagramLink: String,
    @Schema(description = "유튜브 링크")
    val youtubeLink: String,
    @Schema(description = "소개글")
    val introduction: String,
    @Schema(description = "수취인 이름")
    val realName: String?,
    @Schema(description = "은행명")
    val bank: String?,
    @Schema(description = "계좌번호")
    val accountNumber: String?,
    @Schema(description = "전화번호")
    val phoneNumber: String?,
    @Schema(description = "이메일")
    val email: String?,
) {
    companion object {
        fun toMemberProfile(
            member: Member,
            memberInfo: MemberInfo,
            categoryList: List<Category>,
            authorizedMemberId: UUID? = null,
        ) = MemberProfile(
            id = member.id,
            nickname = member.nickname,
            profileImagePath = member.profileImagePath,
            favoriteCategoryList = categoryList,
            blogLink = memberInfo.blogLink,
            instagramLink = memberInfo.instagramLink,
            youtubeLink = memberInfo.youtubeLink,
            introduction = memberInfo.introduction,
            realName = authorizedMemberId?.let { memberInfo.realName },
            bank = authorizedMemberId?.let { memberInfo.bank },
            accountNumber = authorizedMemberId?.let { memberInfo.accountNumber },
            phoneNumber = authorizedMemberId?.let { memberInfo.phoneNumber },
            email = authorizedMemberId?.let { memberInfo.email },
        )
    }
}
