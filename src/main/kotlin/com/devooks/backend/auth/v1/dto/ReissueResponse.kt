package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.domain.TokenGroup

data class ReissueResponse(
    val tokenGroup: TokenGroup
)
