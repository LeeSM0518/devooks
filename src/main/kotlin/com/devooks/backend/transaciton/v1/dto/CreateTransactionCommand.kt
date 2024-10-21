package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import java.util.*

class CreateTransactionCommand(
    val ebookId: UUID,
    val paymentMethod: PaymentMethod,
    val price: Int,
    val requesterId: UUID,
)
