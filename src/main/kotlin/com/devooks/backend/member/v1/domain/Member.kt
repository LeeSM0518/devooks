package com.devooks.backend.member.v1.domain

import com.devooks.backend.auth.v1.domain.Authority
import com.devooks.backend.member.v1.entity.MemberEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

class Member(
    @Schema(description = "회원 식별자")
    val id: UUID,
    @Schema(description = "닉네임")
    val nickname: String,
    @Schema(description = "프로필 사진 경로")
    val profileImagePath: String,
    @Schema(description = "권한 (ex. USER, ADMIN)")
    val authority: Authority,
) {
    companion object {
        fun MemberEntity.toDomain(): Member =
            Member(
                id = this.id!!,
                nickname = this.nickname,
                profileImagePath = this.profileImagePath ?: "",
                authority = this.authority
            )
    }
}
