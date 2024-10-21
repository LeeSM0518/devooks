package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import com.devooks.backend.transaciton.v1.domain.Transaction
import java.time.Instant
import java.util.*

data class CreateTransactionResponse(
    val transactionId: UUID,
    val ebookId: UUID,
    val paymentMethod: PaymentMethod,
    val price: Int,
    val transactionDate: Instant,
) {
    constructor(
        transaction: Transaction,
    ) : this(
        transactionId = transaction.id,
        ebookId = transaction.ebookId,
        paymentMethod = transaction.paymentMethod,
        price = transaction.price,
        transactionDate = transaction.transactionDate,
    )
}
