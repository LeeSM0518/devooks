package com.devooks.backend.member.v1.dto

import com.devooks.backend.auth.v1.domain.OauthId
import com.devooks.backend.auth.v1.domain.OauthType

class SignUpCommand(
    val oauthId: OauthId,
    val oauthType: OauthType,
    val nickname: String,
    val favoriteCategoryNames: List<String>,
)