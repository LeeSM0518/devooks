package com.devooks.backend.member.v1.error

import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.common.error.validateNotNull
import com.devooks.backend.common.error.validateUUID
import java.util.*

private val phoneRegex = Regex("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{3,4}$")
val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

fun String?.validateNickname(): String =
    validateNotBlank(MemberError.REQUIRED_NICKNAME.exception)
        .also {
            it.takeIf { it.length in 2..12 }
                ?: throw MemberError.INVALID_NICKNAME.exception
        }

fun List<String>?.validateFavoriteCategoryIdList(): List<UUID> =
    validateNotNull(MemberError.REQUIRED_FAVORITE_CATEGORIES.exception)
        .map { it.validateUUID(MemberError.INVALID_FAVORITE_CATEGORIES.exception) }

fun String?.validateRealName(): String =
    validateNotBlank(MemberError.REQUIRED_REAL_NAME.exception)

fun String?.validateBank(): String =
    validateNotBlank(MemberError.REQUIRED_BANK.exception)

fun String?.validateAccountNumber(): String =
    validateNotBlank(MemberError.REQUIRED_ACCOUNT_NUMBER.exception)

fun String?.validatePhoneNumber(): String =
    validateNotBlank(MemberError.REQUIRED_PHONE_NUMBER.exception)
        .also { it.takeIf { phoneRegex.matches(it) } ?: throw MemberError.INVALID_PHONE_NUMBER.exception }

fun String?.validateBlogLink(): String =
    validateNotBlank(MemberError.REQUIRED_BLOG_LINK.exception)

fun String?.validateInstagramLink(): String =
    validateNotBlank(MemberError.REQUIRED_INSTAGRAM_LINK.exception)

fun String?.validateYoutubeLink(): String =
    validateNotBlank(MemberError.REQUIRED_YOUTUBE_LINK.exception)

fun String?.validateIntroduction(): String =
    validateNotBlank(MemberError.REQUIRED_INTRODUCTION_LINK.exception)

fun String?.validateWithdrawalReason(): String =
    validateNotBlank(MemberError.REQUIRED_WITHDRAWAL_REASON.exception)

fun String?.validateMemberId(): UUID =
    validateUUID(MemberError.INVALID_MEMBER_ID.exception)

fun String?.validateEmail(): String =
    validateNotBlank(MemberError.REQUIRED_EMAIL.exception)
        .also { it.takeIf { emailRegex.matches(it) } ?: throw MemberError.INVALID_EMAIL.exception }
