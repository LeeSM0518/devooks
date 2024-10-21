package com.devooks.backend.member.v1.domain

import com.devooks.backend.auth.v1.domain.Authority
import com.devooks.backend.member.v1.entity.MemberEntity
import java.util.*

class Member(
    val id: UUID,
    val nickname: String,
    val profileImagePath: String,
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