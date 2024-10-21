package com.devooks.backend.transaciton.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN

enum class TransactionError(val exception: GeneralException) {
    // 400
    REQUIRED_PAYMENT_METHOD(GeneralException("TRANSACTION-400-1", BAD_REQUEST, "결제 수단이 반드시 필요합니다.")),
    INVALID_PAYMENT_METHOD(GeneralException("TRANSACTION-400-2", BAD_REQUEST, "잘못된 형식의 결제 방법 입니다.")),

    // 403
    FORBIDDEN_REVIEW(GeneralException("TRANSACTION-403-1", FORBIDDEN, "구매한 전자책만 리뷰가 가능합니다.")),

    // 409
    DUPLICATE_TRANSACTION(GeneralException("TRANSACTION-409-1", CONFLICT, "이미 구매한 책 입니다."))
    ;

    override fun toString(): String {
        return super.toString()
    }
}
