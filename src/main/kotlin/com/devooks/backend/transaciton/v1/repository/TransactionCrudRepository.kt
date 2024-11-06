package com.devooks.backend.transaciton.v1.repository

import com.devooks.backend.transaciton.v1.entity.TransactionEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionCrudRepository : CoroutineCrudRepository<TransactionEntity, UUID> {

    suspend fun existsByEbookIdAndBuyerMemberId(ebookId: UUID, buyerMemberId: UUID): Boolean
}
