package com.devooks.backend.member.v1.dto

import com.devooks.backend.auth.v1.domain.OauthType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class SignUpRequest(
    @field:NotBlank
    @Schema(description = "OAuth2 식별자", required = true, implementation = UUID::class)
    val oauthId: String,
    @Schema(description = "OAuth2 인증 유형", required = true)
    val oauthType: OauthType,
    @field:Size(min = 2, max = 12)
    @Schema(description = "닉네임", required = true)
    val nickname: String,
    @Schema(description = "관심 카테고리 식별자 목록", required = true)
    val favoriteCategoryIdList: List<UUID>,
) {

    fun toCommand(): SignUpCommand =
        SignUpCommand(
            oauthId = oauthId,
            oauthType = oauthType,
            nickname = nickname,
            favoriteCategoryIdList = favoriteCategoryIdList
        )

}
