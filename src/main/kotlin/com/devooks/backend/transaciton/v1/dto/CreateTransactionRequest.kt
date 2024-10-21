package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.ebook.v1.error.validateEbookPrice
import com.devooks.backend.transaciton.v1.error.validatePaymentMethod
import com.devooks.backend.wishlist.v1.error.validateEbookId
import java.util.*

data class CreateTransactionRequest(
    val ebookId: String?,
    val paymentMethod: String?,
    val price: Int?
) {
    fun toCommand(requesterId: UUID): CreateTransactionCommand =
        CreateTransactionCommand(
            ebookId = ebookId.validateEbookId(),
            paymentMethod = paymentMethod.validatePaymentMethod(),
            price = price.validateEbookPrice(),
            requesterId = requesterId
        )
}
