package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.error.validateAuthorizationCode
import com.devooks.backend.auth.v1.error.validateOauthType
import io.swagger.v3.oas.annotations.media.Schema

data class LoginRequest(
    @Schema(description = "OAuth2 인증 코드", required = true, nullable = false)
    val authorizationCode: String?,
    @Schema(
        description = "OAuth2 인증 유형 (ex. NAVER, KAKAO, GOOGLE)",
        required = true,
        nullable = false,
        example = "NAVER"
    )
    val oauthType: String?,
) {
    fun toCommand(): LoginCommand =
        LoginCommand(
            authorizationCode = authorizationCode.validateAuthorizationCode(),
            oauthType = oauthType.validateOauthType()
        )
}
