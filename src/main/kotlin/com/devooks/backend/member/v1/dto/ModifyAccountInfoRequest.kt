package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.error.validateAccountNumber
import com.devooks.backend.member.v1.error.validateBank
import com.devooks.backend.member.v1.error.validateRealName

data class ModifyAccountInfoRequest(
    val realName: String?,
    val bank: String?,
    val accountNumber: String?,
) {

    fun toCommand(): ModifyAccountInfoCommand =
        ModifyAccountInfoCommand(
            realName = realName.validateRealName(),
            bank = bank.validateBank(),
            accountNumber = accountNumber.validateAccountNumber()
        )

}
