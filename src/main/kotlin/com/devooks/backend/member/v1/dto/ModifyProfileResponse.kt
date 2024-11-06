package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.domain.MemberInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class ModifyProfileResponse(
    val memberInfo: MemberInfo,
    @Schema(description = "관심 카테고리 식별자 목록")
    val favoriteCategoryIdList: List<UUID>,
)
