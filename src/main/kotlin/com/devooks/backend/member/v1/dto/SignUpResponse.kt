package com.devooks.backend.member.v1.dto

import com.devooks.backend.auth.v1.domain.Authority
import com.devooks.backend.auth.v1.domain.TokenGroup
import java.util.*

data class SignUpResponse(
    val member: Member,
    val tokenGroup: TokenGroup,
) {
    data class Member(
        val id: UUID,
        val nickname: String,
        val profileImagePath: String,
        val authority: Authority,
    ) {
        constructor(
            member: com.devooks.backend.member.v1.domain.Member,
        ) : this(
            id = member.id,
            nickname = member.nickname,
            profileImagePath = member.profileImagePath,
            authority = member.authority
        )
    }
}
