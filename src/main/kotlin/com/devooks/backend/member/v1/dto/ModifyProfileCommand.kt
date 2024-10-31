package com.devooks.backend.member.v1.dto

import java.util.*

class ModifyProfileCommand(
    val phoneNumber: String?,
    val blogLink: String?,
    val instagramLink: String?,
    val youtubeLink: String?,
    val introduction: String?,
    val favoriteCategoryIdList: List<UUID>?,
    val email: String?,
)
