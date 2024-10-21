package com.devooks.backend.member.v1.dto

class ModifyProfileCommand(
    val phoneNumber: String,
    val blogLink: String,
    val instagramLink: String,
    val youtubeLink: String,
    val introduction: String,
    val favoriteCategoryNames: List<String>,
)
