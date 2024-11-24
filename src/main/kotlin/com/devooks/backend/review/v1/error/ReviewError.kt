package com.devooks.backend.review.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class ReviewError(val exception: GeneralException) {
    // 403
    FORBIDDEN_MODIFY_REVIEW(GeneralException("REVIEW-403-1", FORBIDDEN, "자신이 작성한 리뷰만 수정할 수 있습니다.")),
    FORBIDDEN_MODIFY_REVIEW_COMMENT(GeneralException("REVIEW-403-2", FORBIDDEN, "자신이 작성한 리뷰 댓글만 수정할 수 있습니다.")),

    // 404
    NOT_FOUND_REVIEW(GeneralException("REVIEW-404-1", NOT_FOUND, "존재하지 않는 리뷰입니다.")),
    NOT_FOUND_REVIEW_COMMENT(GeneralException("REVIEW-404-2", NOT_FOUND, "존재하지 않는 리뷰 댓글입니다.")),

    // 409
    DUPLICATE_REVIEW(GeneralException("REVIEW-409-1", CONFLICT, "이미 작성한 리뷰입니다."))
    ;

}
