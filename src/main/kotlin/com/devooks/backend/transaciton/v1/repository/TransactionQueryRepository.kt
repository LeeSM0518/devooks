package com.devooks.backend.transaciton.v1.repository

import com.devooks.backend.transaciton.v1.domain.PaymentMethod.Companion.toPaymentMethod
import com.devooks.backend.transaciton.v1.domain.Transaction
import com.devooks.backend.transaciton.v1.dto.GetBuyHistoriesCommand
import com.devooks.backend.transaciton.v1.dto.GetSellHistoriesCommand
import io.r2dbc.spi.Readable
import java.math.BigInteger
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class TransactionQueryRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findBy(command: GetBuyHistoriesCommand): List<Transaction> {
        val bindings = mutableMapOf<String, Any>()
        val query = """
            SELECT t.*
            FROM transaction t, ebook e
            WHERE t.ebook_id = e.ebook_id
            AND t.buyer_member_id = ${
            command.requesterId.let {
                bindings["requesterId"] = it
                ":requesterId"
            }
        }
            ${
            command.ebookTitle?.let {
                bindings["ebookTitle"] = it
                "AND e.title ilike :ebookTitle"
            } ?: ""
        }
            ORDER BY t.transaction_date DESC
            OFFSET ${command.offset} LIMIT ${command.limit};
        """.trimIndent()

        return databaseClient
            .sql(query)
            .bindValues(bindings)
            .map { row -> mapToDomain(row) }
            .all()
            .asFlow()
            .toList()
    }

    suspend fun findBy(command: GetSellHistoriesCommand): List<Transaction> {
        val bindings = mutableMapOf<String, Any>()
        val query = """
            SELECT t.*
            FROM transaction t, ebook e
            WHERE t.ebook_id = e.ebook_id
            AND e.selling_member_id = ${
            command.requesterId.let {
                bindings["requesterId"] = it
                ":requesterId"
            }
        }
            ORDER BY t.transaction_date DESC
            OFFSET ${command.offset} LIMIT ${command.limit}
        """.trimIndent()

        return databaseClient
            .sql(query)
            .bindValues(bindings)
            .map { row -> mapToDomain(row) }
            .all()
            .asFlow()
            .toList()
    }

    private fun mapToDomain(row: Readable) = Transaction(
        id = row.get("transaction_id", UUID::class.java)!!,
        ebookId = row.get("ebook_id", UUID::class.java)!!,
        price = row.get("price", BigInteger::class.java)!!.toInt(),
        paymentMethod = row.get("payment_method", String::class.java)!!.toPaymentMethod(),
        transactionDate = row.get("transaction_date", Instant::class.java)!!,
        buyerMemberId = row.get("buyer_member_id", UUID::class.java)!!,
    )

}
