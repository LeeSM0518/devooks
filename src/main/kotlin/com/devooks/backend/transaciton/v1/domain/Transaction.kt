package com.devooks.backend.transaciton.v1.domain

import java.time.Instant
import java.util.*

class Transaction(
    val id: UUID,
    val ebookId: UUID,
    val price: Int,
    val paymentMethod: PaymentMethod,
    val transactionDate: Instant,
    val buyerMemberId: UUID,
)
