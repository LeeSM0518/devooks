package com.devooks.backend.ebook.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class EbookError(val exception: GeneralException) {
    // 400
    INVALID_EBOOK_PRICE(GeneralException("EBOOK-400-5", BAD_REQUEST, "유효하지 않은 가격입니다.")),
    INVALID_TOP_100(GeneralException("EBOOK-400-8", BAD_REQUEST, "잘못된 형식의 TOP100(ex. DAILY, WEEKLY, MONTHLY) 입니다.")),
    INVALID_EBOOK_ORDER(GeneralException("EBOOK-400-9", BAD_REQUEST, "잘못된 형식의 EbookOrder(ex. LATEST, REVIEW) 입니다.")),
    INVALID_EBOOK_ID(GeneralException("EBOOK-400-16", BAD_REQUEST, "잘못된 형식의 전자책 식별자입니다.")),
    REQUIRED_EBOOK_ID(GeneralException("EBOOK-400-23", BAD_REQUEST, "전자책 식별자가 반드시 필요합니다.")),

    // 403
    FORBIDDEN_BUYER_MEMBER_ID(GeneralException("EBOOK-403-1", FORBIDDEN, "자신의 책을 구매하는 것은 불가능합니다.")),
    FORBIDDEN_MODIFY_EBOOK_INQUIRY(GeneralException("EBOOK-403-2", FORBIDDEN, "자신이 작성한 문의만 수정할 수 있습니다.")),
    FORBIDDEN_MODIFY_EBOOK_INQUIRY_COMMENT(GeneralException("EBOOK-403-3", FORBIDDEN, "자신이 작성한 댓글만 수정할 수 있습니다.")),
    FORBIDDEN_MODIFY_EBOOK(GeneralException("EBOOK-403-4", FORBIDDEN, "자신이 등록한 전자책만 수정할 수 있습니다.")),
    FORBIDDEN_REGISTER_EBOOK_TO_IMAGE(GeneralException("EBOOK-403-5", FORBIDDEN, "자신이 등록한 사진만 전자책에 등록할 수 있습니다.")),
    FORBIDDEN_DELETE_EBOOK(GeneralException("EBOOK-403-6", FORBIDDEN, "자신이 등록한 전자책만 삭제할 수 있습니다.")),

    // 404
    NOT_FOUND_EBOOK(GeneralException("EBOOK-404-1", NOT_FOUND, "전자책을 찾을 수 없습니다.")),
    NOT_FOUND_EBOOK_INQUIRY(GeneralException("EBOOK-404-2", NOT_FOUND, "문의을 찾을 수 없습니다.")),
    NOT_FOUND_EBOOK_INQUIRY_COMMENT(GeneralException("EBOOK-404-3", NOT_FOUND, "댓글을 찾을 수 없습니다.")),
    NOT_FOUND_MAIN_IMAGE(GeneralException("EBOOK-404-4", NOT_FOUND, "메인 사진을 찾을 수 없습니다.")),
    NOT_FOUND_DESCRIPTION_IMAGE(GeneralException("EBOOK-404-5", NOT_FOUND, "설명 사진을 찾을 수 없습니다.")),
}
