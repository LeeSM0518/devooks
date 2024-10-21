package com.devooks.backend.member.v1.dto

import com.devooks.backend.member.v1.error.validateWithdrawalReason

data class WithdrawMemberRequest(
    val withdrawalReason: String?
) {

    fun toCommand(): WithdrawMemberCommand =
        WithdrawMemberCommand(
            withdrawalReason = withdrawalReason.validateWithdrawalReason()
        )
}
