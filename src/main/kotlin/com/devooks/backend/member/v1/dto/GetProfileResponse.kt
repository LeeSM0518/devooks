package com.devooks.backend.member.v1.dto

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.MemberInfo
import com.devooks.backend.member.v1.dto.MemberProfile.Companion.toMemberProfile
import java.util.*

data class GetProfileResponse(
    val profile: MemberProfile,
) {

    constructor(
        member: Member,
        memberInfo: MemberInfo,
        categories: List<Category>,
        authorizedMemberId: UUID? = null
    ) : this(
        profile = toMemberProfile(
            member = member,
            memberInfo = memberInfo,
            categoryList = categories,
            authorizedMemberId = authorizedMemberId
        )
    )
}
