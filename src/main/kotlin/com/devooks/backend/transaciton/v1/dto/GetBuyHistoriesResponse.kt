package com.devooks.backend.transaciton.v1.dto

import com.devooks.backend.transaciton.v1.domain.Transaction
import com.devooks.backend.transaciton.v1.dto.TransactionDto.Companion.toDto

data class GetBuyHistoriesResponse(
    val transactionList: List<TransactionDto>,
) {

    companion object {
        fun List<Transaction>.toGetBuyHistoriesResponse(): GetBuyHistoriesResponse =
            GetBuyHistoriesResponse(this.map { it.toDto() })
    }
}
