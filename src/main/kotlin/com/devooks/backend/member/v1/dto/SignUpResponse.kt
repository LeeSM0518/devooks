package com.devooks.backend.member.v1.dto

import com.devooks.backend.auth.v1.domain.Authority
import com.devooks.backend.auth.v1.domain.TokenGroup
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class SignUpResponse(
    val member: Member,
    val tokenGroup: TokenGroup,
) {
    data class Member(
        @Schema(description = "회원 식별자")
        val id: UUID,
        @Schema(description = "닉네임")
        val nickname: String,
        @Schema(description = "프로필 사진 경로")
        val profileImagePath: String,
        @Schema(description = "권한 (ex. USER, ADMIN)")
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
