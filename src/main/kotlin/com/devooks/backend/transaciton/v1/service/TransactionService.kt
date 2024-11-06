package com.devooks.backend.transaciton.v1.service

import com.devooks.backend.review.v1.dto.CreateReviewCommand
import com.devooks.backend.transaciton.v1.domain.Transaction
import com.devooks.backend.transaciton.v1.dto.CreateTransactionCommand
import com.devooks.backend.transaciton.v1.dto.GetBuyHistoriesCommand
import com.devooks.backend.transaciton.v1.dto.GetSellHistoriesCommand
import com.devooks.backend.transaciton.v1.entity.TransactionEntity
import com.devooks.backend.transaciton.v1.error.TransactionError
import com.devooks.backend.transaciton.v1.repository.TransactionCrudRepository
import com.devooks.backend.transaciton.v1.repository.TransactionQueryRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionCrudRepository: TransactionCrudRepository,
    private val transactionQueryRepository: TransactionQueryRepository,
) {
    suspend fun create(command: CreateTransactionCommand): Transaction {
        validateCreateCommand(command)
        val entity = TransactionEntity(
            ebookId = command.ebookId,
            price = command.price,
            paymentMethod = command.paymentMethod,
            buyerMemberId = command.requesterId
        )
        return transactionCrudRepository.save(entity).toDomain()
    }

    suspend fun get(command: GetBuyHistoriesCommand): List<Transaction> =
        transactionQueryRepository.findBy(command).toList()

    suspend fun get(command: GetSellHistoriesCommand): List<Transaction> =
        transactionQueryRepository.findBy(command).toList()

    suspend fun validate(command: CreateReviewCommand) {
        transactionCrudRepository
            .existsByEbookIdAndBuyerMemberId(command.ebookId, command.requesterId)
            .takeIf { it }
            ?: throw TransactionError.FORBIDDEN_REVIEW.exception
    }

    private suspend fun validateCreateCommand(command: CreateTransactionCommand) {
        transactionCrudRepository
            .existsByEbookIdAndBuyerMemberId(command.ebookId, command.requesterId)
            .takeIf { it.not() }
            ?: throw TransactionError.DUPLICATE_TRANSACTION.exception
    }

}
