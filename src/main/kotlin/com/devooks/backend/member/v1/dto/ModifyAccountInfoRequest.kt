package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.error.validateAccountNumber
import com.devooks.backend.member.v1.error.validateBank
import com.devooks.backend.member.v1.error.validateRealName
import io.swagger.v3.oas.annotations.media.Schema

data class ModifyAccountInfoRequest(
    @Schema(description = "수취인 이름", required = true, nullable = false)
    val realName: String?,
    @Schema(description = "은행 이름", required = true, nullable = false)
    val bank: String?,
    @Schema(description = "계좌 번호", required = true, nullable = false)
    val accountNumber: String?,
) {

    fun toCommand(): ModifyAccountInfoCommand =
        ModifyAccountInfoCommand(
            realName = realName.validateRealName(),
            bank = bank.validateBank(),
            accountNumber = accountNumber.validateAccountNumber()
        )

}
