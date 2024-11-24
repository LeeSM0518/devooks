package com.devooks.backend.member.v1.error

import com.devooks.backend.common.error.validateUUID
import java.util.*

fun String?.validateMemberId(): UUID =
    validateUUID(MemberError.INVALID_MEMBER_ID.exception)

