package com.devooks.backend.member.v1.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ModifyAccountInfoRequest(
    @field:Size(min = 1, max = 10)
    @Schema(description = "수취인 이름", nullable = true)
    val realName: String?,
    @field:Size(min = 1, max = 10)
    @Schema(description = "은행 이름", nullable = true)
    val bank: String?,
    @field:Size(min = 1, max = 20)
    @field:Pattern(regexp = "[0-9]+")
    @Schema(description = "계좌 번호", nullable = true)
    val accountNumber: String?,
) {

    fun toCommand(): ModifyAccountInfoCommand =
        ModifyAccountInfoCommand(
            realName = realName,
            bank = bank,
            accountNumber = accountNumber
        )

}
