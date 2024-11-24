package com.devooks.backend.member.v1.dto

import jakarta.validation.constraints.Size

data class WithdrawMemberRequest(
    @field:Size(min = 1, max = 255)
    val withdrawalReason: String
) {

    fun toCommand(): WithdrawMemberCommand =
        WithdrawMemberCommand(withdrawalReason = withdrawalReason)
}
