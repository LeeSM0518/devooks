package com.devooks.backend.transaciton.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.ebook.v1.service.EbookService
import com.devooks.backend.transaciton.v1.domain.Transaction
import com.devooks.backend.transaciton.v1.dto.CreateTransactionCommand
import com.devooks.backend.transaciton.v1.dto.CreateTransactionRequest
import com.devooks.backend.transaciton.v1.dto.CreateTransactionResponse
import com.devooks.backend.transaciton.v1.dto.GetBuyHistoriesCommand
import com.devooks.backend.transaciton.v1.dto.GetBuyHistoriesResponse
import com.devooks.backend.transaciton.v1.dto.GetBuyHistoriesResponse.Companion.toGetBuyHistoriesResponse
import com.devooks.backend.transaciton.v1.dto.GetSellHistoriesCommand
import com.devooks.backend.transaciton.v1.dto.GetSellHistoriesResponse
import com.devooks.backend.transaciton.v1.dto.GetSellHistoriesResponse.Companion.toGetSellHistoriesResponse
import com.devooks.backend.transaciton.v1.service.TransactionService
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val ebookService: EbookService,
    private val tokenService: TokenService,
) {

    @Transactional
    @PostMapping
    suspend fun createTransaction(
        @RequestBody
        request: CreateTransactionRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): CreateTransactionResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: CreateTransactionCommand = request.toCommand(requesterId)
        ebookService.validate(command)
        val transaction: Transaction = transactionService.create(command)
        return CreateTransactionResponse(transaction)
    }

    @GetMapping("/buy-histories")
    suspend fun getBuyHistories(
        @RequestParam(required = false, defaultValue = "")
        ebookTitle: String,
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): GetBuyHistoriesResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = GetBuyHistoriesCommand(ebookTitle, page, count, requesterId)
        return transactionService.get(command).toGetBuyHistoriesResponse()
    }

    @GetMapping("/sell-histories")
    suspend fun getSellHistories(
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): GetSellHistoriesResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = GetSellHistoriesCommand(page, count, requesterId)
        return transactionService.get(command).toGetSellHistoriesResponse()
    }
}