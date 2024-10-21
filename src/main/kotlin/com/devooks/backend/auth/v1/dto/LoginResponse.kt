package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.domain.TokenGroup
import com.devooks.backend.member.v1.domain.Member

data class LoginResponse(
    val member: Member,
    val tokenGroup: TokenGroup,
)
