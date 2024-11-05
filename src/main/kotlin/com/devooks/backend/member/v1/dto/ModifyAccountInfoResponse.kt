package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.domain.MemberInfo
import io.swagger.v3.oas.annotations.media.Schema

data class ModifyAccountInfoResponse(
    @Schema(description = "수취인 이름")
    val realName: String,
    @Schema(description = "은행 이름")
    val bank: String,
    @Schema(description = "계좌 번호")
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
