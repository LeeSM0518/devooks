package com.devooks.backend.transaciton.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.jooq.tables.references.EBOOK
import com.devooks.backend.jooq.tables.references.TRANSACTION
import com.devooks.backend.transaciton.v1.domain.Transaction
import com.devooks.backend.transaciton.v1.dto.GetBuyHistoriesCommand
import com.devooks.backend.transaciton.v1.dto.GetSellHistoriesCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class TransactionQueryRepository : JooqR2dbcRepository() {

    suspend fun findBy(command: GetBuyHistoriesCommand): Flow<Transaction> =
        query {
            select(
                TRANSACTION.TRANSACTION_ID.`as`("id"),
                TRANSACTION.EBOOK_ID,
                TRANSACTION.PRICE,
                TRANSACTION.PAYMENT_METHOD,
                TRANSACTION.TRANSACTION_DATE,
                TRANSACTION.BUYER_MEMBER_ID
            ).from(
                TRANSACTION
                    .join(EBOOK).on(EBOOK.EBOOK_ID.eq(TRANSACTION.EBOOK_ID))
            ).where(TRANSACTION.BUYER_MEMBER_ID.eq(command.requesterId)).apply {
                command.ebookTitle?.let { ebookTitle ->
                    and(EBOOK.TITLE.likeIgnoreCase(ebookTitle))
                }
            }.orderBy(
                TRANSACTION.TRANSACTION_DATE.desc()
            ).offset(command.offset).limit(command.limit)
        }.map { it.into(Transaction::class.java) }

    suspend fun findBy(command: GetSellHistoriesCommand): Flow<Transaction> =
        query {
            select(
                TRANSACTION.TRANSACTION_ID.`as`("id"),
                TRANSACTION.EBOOK_ID,
                TRANSACTION.PRICE,
                TRANSACTION.PAYMENT_METHOD,
                TRANSACTION.TRANSACTION_DATE,
                TRANSACTION.BUYER_MEMBER_ID
            ).from(
                TRANSACTION
                    .join(EBOOK).on(EBOOK.EBOOK_ID.eq(TRANSACTION.EBOOK_ID))
            ).where(
                EBOOK.SELLING_MEMBER_ID.eq(command.requesterId)
            ).orderBy(
                TRANSACTION.TRANSACTION_DATE.desc()
            ).offset(command.offset).limit(command.limit)
        }.map { it.into(Transaction::class.java) }

}
