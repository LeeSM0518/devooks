package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.error.validateBlogLink
import com.devooks.backend.member.v1.error.validateEmail
import com.devooks.backend.member.v1.error.validateFavoriteCategoryIdList
import com.devooks.backend.member.v1.error.validateInstagramLink
import com.devooks.backend.member.v1.error.validateIntroduction
import com.devooks.backend.member.v1.error.validatePhoneNumber
import com.devooks.backend.member.v1.error.validateYoutubeLink

data class ModifyProfileRequest(
    val phoneNumber: String?,
    val blogLink: String?,
    val instagramLink: String?,
    val youtubeLink: String?,
    val introduction: String?,
    val favoriteCategoryIdList: List<String>?,
    val email: String?,
) {
    fun toCommand(): ModifyProfileCommand =
        ModifyProfileCommand(
            phoneNumber = phoneNumber?.validatePhoneNumber(),
            blogLink = blogLink?.validateBlogLink(),
            instagramLink = instagramLink?.validateInstagramLink(),
            youtubeLink = youtubeLink?.validateYoutubeLink(),
            introduction = introduction?.validateIntroduction(),
            favoriteCategoryIdList = favoriteCategoryIdList?.validateFavoriteCategoryIdList(),
            email = email?.validateEmail(),
        )
}
