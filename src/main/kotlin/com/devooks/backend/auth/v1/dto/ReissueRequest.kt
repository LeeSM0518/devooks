package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.error.validateRefreshToken

data class ReissueRequest(
    val refreshToken: String?
) {
    fun toCommand(): ReissueCommand =
        ReissueCommand(
            refreshToken = refreshToken.validateRefreshToken()
        )
}
