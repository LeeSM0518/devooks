package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.domain.RefreshToken

data class ReissueCommand(
    val refreshToken: RefreshToken
)
