package com.devooks.backend.transaciton.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN

enum class TransactionError(val exception: GeneralException) {
    // 403
    FORBIDDEN_REVIEW(GeneralException("TRANSACTION-403-1", FORBIDDEN, "구매한 전자책만 리뷰가 가능합니다.")),

    // 409
    DUPLICATE_TRANSACTION(GeneralException("TRANSACTION-409-1", CONFLICT, "이미 구매한 책 입니다."))
    ;
}
