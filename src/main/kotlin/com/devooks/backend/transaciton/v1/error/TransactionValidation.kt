package com.devooks.backend.transaciton.v1.error

import com.devooks.backend.common.error.validateNotBlank
import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import com.devooks.backend.transaciton.v1.domain.PaymentMethod.Companion.toPaymentMethod

fun String?.validatePaymentMethod(): PaymentMethod =
    validateNotBlank(TransactionError.REQUIRED_PAYMENT_METHOD.exception)
        .toPaymentMethod()