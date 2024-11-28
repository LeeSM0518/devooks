package com.devooks.backend.wishlist.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class WishlistError(val exception: GeneralException) {
    // 403
    FORBIDDEN_DELETE_WISHLIST(GeneralException("WISHLIST-403-1", FORBIDDEN, "자신의 찜만 삭제할 수 있습니다.")),

    // 404
    NOT_FOUND_WISHLIST(GeneralException("WISHLIST-404-1", NOT_FOUND, "존재하지 않는 찜입니다.")),

    // 409
    DUPLICATE_WISHLIST(GeneralException("WISHLIST-409-1", CONFLICT, "이미 존재하는 찜입니다."))
    ;

    override fun toString(): String {
        return "WishlistError(exception=$exception)"
    }
}
