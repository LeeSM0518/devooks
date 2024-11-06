package com.devooks.backend.member.v1.dto

import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.member.v1.domain.MemberInfo

data class ModifyProfileResponse(
    val memberInfo: MemberInfo,
    val favoriteCategoryList: List<Category>,
)
