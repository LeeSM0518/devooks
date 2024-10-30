package com.devooks.backend.member.v1.error

import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.common.error.validateNotNull
import com.devooks.backend.common.error.validateUUID
import java.util.*

private val phoneRegex = Regex("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{3,4}$")

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
    validateNotNull(MemberError.REQUIRED_PHONE_NUMBER.exception)
        .takeIf { it.isNotBlank() }
        ?.also {
            it.takeIf { phoneRegex.matches(it) }
                ?: throw MemberError.INVALID_PHONE_NUMBER.exception
        }
        ?: ""

fun String?.validateBlogLink(): String =
    validateNotNull(MemberError.REQUIRED_BLOG_LINK.exception)
        .takeIf { it.isNotBlank() }
        ?: ""

fun String?.validateInstagramLink(): String =
    validateNotNull(MemberError.REQUIRED_INSTAGRAM_LINK.exception)
        .takeIf { it.isNotBlank() }
        ?: ""

fun String?.validateYoutubeLink(): String =
    validateNotNull(MemberError.REQUIRED_YOUTUBE_LINK.exception)
        .takeIf { it.isNotBlank() }
        ?: ""

fun String?.validateIntroduction(): String =
    validateNotNull(MemberError.REQUIRED_INTRODUCTION_LINK.exception)
        .takeIf { it.isNotBlank() }
        ?: ""

fun String?.validateWithdrawalReason(): String =
    validateNotBlank(MemberError.REQUIRED_WITHDRAWAL_REASON.exception)

fun String?.validateMemberId(): UUID =
    validateUUID(MemberError.INVALID_MEMBER_ID.exception)
