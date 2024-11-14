package com.devooks.backend.category.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND

enum class CategoryError(val exception: GeneralException) {
    // 400
    INVALID_CATEGORY_ID(GeneralException("CATEGORY-400-1", BAD_REQUEST, "잘못된 형식의 카테고리 식별자 입니다")),
    // 404
    NOT_FOUND_CATEGORY_BY_ID(GeneralException("CATEGORY-404-1", NOT_FOUND, "카테고리를 찾을 수 없습니다"))
}
