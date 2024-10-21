package com.devooks.backend.transaciton.v1.domain

import com.devooks.backend.transaciton.v1.domain.PaymentMethod.BANK_DEPOSIT
import com.devooks.backend.transaciton.v1.domain.PaymentMethod.CREDIT_CARD
import com.devooks.backend.transaciton.v1.domain.PaymentMethod.MOBILE_PHONE
import com.devooks.backend.transaciton.v1.domain.PaymentMethod.REAL_TIME_BANK_TRANSFER
import com.devooks.backend.transaciton.v1.error.TransactionError

/**
 * 결제 방법
 *
 * @property CREDIT_CARD 신용카드
 * @property REAL_TIME_BANK_TRANSFER 실시간 계좌이체
 * @property BANK_DEPOSIT 무통장 입금
 * @property MOBILE_PHONE 휴대폰
 *
 */
enum class PaymentMethod {
    CREDIT_CARD, REAL_TIME_BANK_TRANSFER, BANK_DEPOSIT, MOBILE_PHONE;

    companion object {
        fun String.toPaymentMethod(): PaymentMethod =
            runCatching {
                PaymentMethod.valueOf(this)
            }.getOrElse {
                throw TransactionError.INVALID_PAYMENT_METHOD.exception
            }
    }
}