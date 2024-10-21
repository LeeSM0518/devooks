package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.transaciton.v1.domain.Transaction
import com.devooks.backend.transaciton.v1.dto.TransactionDto.Companion.toDto

data class GetSellHistoriesResponse(
    val transactionList: List<TransactionDto>,
) {

    companion object {
        fun List<Transaction>.toGetSellHistoriesResponse(): GetSellHistoriesResponse =
            GetSellHistoriesResponse(this.map { it.toDto() })
    }
}
