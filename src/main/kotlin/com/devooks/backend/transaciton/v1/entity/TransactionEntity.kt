package com.devooks.backend.transaciton.v1.entity

import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import com.devooks.backend.transaciton.v1.domain.Transaction
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "transaction")
data class TransactionEntity(
    @Id
    @Column(value = "transaction_id")
    @get:JvmName("transactionId")
    val id: UUID? = null,
    val ebookId: UUID,
    val price: Int,
    val paymentMethod: PaymentMethod,
    val buyerMemberId: UUID,
    val transactionDate: Instant = Instant.now(),
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        Transaction(
            id = this.id!!,
            ebookId = this.ebookId,
            price = this.price,
            paymentMethod = this.paymentMethod,
            transactionDate = this.transactionDate,
            buyerMemberId = this.buyerMemberId,
        )
}
