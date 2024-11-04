package com.devooks.backend.wishlist.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class WishlistError(val exception: GeneralException) {
    // 400
    INVALID_CATEGORY_ID(GeneralException("WISHLIST-400-1", BAD_REQUEST, "잘못된 형식의 카테고리 식별자 입니다.")),
    INVALID_WISHLIST_ID(GeneralException("WISHLIST-400-2", BAD_REQUEST, "잘못된 형식의 찜 식별자 입니다.")),

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
