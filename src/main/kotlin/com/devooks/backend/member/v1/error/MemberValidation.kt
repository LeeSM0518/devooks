package com.devooks.backend.member.v1.error

import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.common.error.validateUUID
import java.util.*

val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

fun String?.validateMemberId(): UUID =
    validateUUID(MemberError.INVALID_MEMBER_ID.exception)

fun String?.validateEmail(): String =
    validateNotBlank(MemberError.REQUIRED_EMAIL.exception)
        .also { it.takeIf { emailRegex.matches(it) } ?: throw MemberError.INVALID_EMAIL.exception }
