package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.domain.MemberInfo

data class ModifyAccountInfoResponse(
    val realName: String,
    val bank: String,
    val accountNumber: String,
) {

    constructor(
        memberInfo: MemberInfo,
    ) : this(
        realName = memberInfo.realName,
        bank = memberInfo.bank,
        accountNumber = memberInfo.accountNumber,
    )

}
