package com.devooks.backend.auth.v1.dto

import com.devooks.backend.auth.v1.domain.OauthType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    @Schema(description = "OAuth2 인증 코드", required = true)
    val authorizationCode: String,
    @Schema(description = "OAuth2 인증 유형 (ex. NAVER, KAKAO, GOOGLE)", required = true)
    val oauthType: OauthType,
) {
    fun toCommand(): LoginCommand =
        LoginCommand(
            authorizationCode = authorizationCode,
            oauthType = oauthType
        )
}
