package com.devooks.backend.auth.v1.domain

typealias OauthId = String

data class OauthInfo(
    val oauthId: OauthId,
    val oauthType: OauthType
)