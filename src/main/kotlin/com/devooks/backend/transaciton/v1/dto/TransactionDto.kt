package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.transaciton.v1.domain.Transaction
import java.time.Instant
import java.util.*

data class TransactionDto(
    val id: UUID,
    val ebookId: UUID,
    val transactionDate: Instant,
    val price: Int,
) {
    companion object {
        fun Transaction.toDto(): TransactionDto =
            TransactionDto(this.id, this.ebookId, this.transactionDate, this.price)
    }
}
