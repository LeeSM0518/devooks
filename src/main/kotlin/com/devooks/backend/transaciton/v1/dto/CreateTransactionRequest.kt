package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.util.*

data class CreateTransactionRequest(
    val ebookId: UUID,
    val paymentMethod: PaymentMethod,
    @field:Min(0)
    @field:Max(10_000_000)
    val price: Int
) {
    fun toCommand(requesterId: UUID): CreateTransactionCommand =
        CreateTransactionCommand(
            ebookId = ebookId,
            paymentMethod = paymentMethod,
            price = price,
            requesterId = requesterId
        )
}
