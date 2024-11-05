package com.devooks.backend.member.v1.dto

import com.devooks.backend.auth.v1.error.validateOauthId
import com.devooks.backend.auth.v1.error.validateOauthType
import com.devooks.backend.member.v1.error.validateFavoriteCategoryIdList
import com.devooks.backend.member.v1.error.validateNickname
import io.swagger.v3.oas.annotations.media.Schema

data class SignUpRequest(
    @Schema(description = "OAuth2 식별자", required = true, nullable = false)
    val oauthId: String?,
    @Schema(
        description = "OAuth2 인증 유형 (ex. NAVER, KAKAO, GOOGLE)",
        required = true,
        nullable = false,
        example = "NAVER"
    )
    val oauthType: String?,
    @Schema(description = "닉네임", required = true, nullable = false)
    val nickname: String?,
    @Schema(description = "관심 카테고리 식별자 목록", required = true, nullable = false)
    val favoriteCategoryIdList: List<String>?,
) {

    fun toCommand(): SignUpCommand =
        SignUpCommand(
            oauthId = oauthId.validateOauthId(),
            oauthType = oauthType.validateOauthType(),
            nickname = nickname.validateNickname(),
            favoriteCategoryIdList = favoriteCategoryIdList.validateFavoriteCategoryIdList()
        )

}
