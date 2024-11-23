package com.devooks.backend.ebook.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class EbookError(val exception: GeneralException) {
    // 400
    REQUIRED_PDF_ID(GeneralException("EBOOK-400-1", BAD_REQUEST, "PDF 식별자가 반드시 필요합니다.")),
    INVALID_PDF_ID(GeneralException("EBOOK-400-2", BAD_REQUEST, "잘못된 형식의 PDF 식별자 입니다.")),
    REQUIRED_TITLE(GeneralException("EBOOK-400-3", BAD_REQUEST, "전자책 제목이 반드시 필요합니다.")),
    REQUIRED_RELATED_CATEGORY_LIST(GeneralException("EBOOK-400-4", BAD_REQUEST, "관련 카테고리가 반드시 필요합니다.")),
    INVALID_EBOOK_PRICE(GeneralException("EBOOK-400-5", BAD_REQUEST, "유효하지 않은 가격입니다.")),
    REQUIRED_EBOOK_INTRODUCTION(GeneralException("EBOOK-400-6", BAD_REQUEST, "전자책 소개가 반드시 필요합니다.")),
    REQUIRED_TABLE_OF_CONTENTS(GeneralException("EBOOK-400-7", BAD_REQUEST, "목차가 반드시 필요합니다.")),
    INVALID_TOP_100(GeneralException("EBOOK-400-8", BAD_REQUEST, "잘못된 형식의 TOP100(ex. DAILY, WEEKLY, MONTHLY) 입니다.")),
    INVALID_EBOOK_ORDER(GeneralException("EBOOK-400-9", BAD_REQUEST, "잘못된 형식의 EbookOrder(ex. LATEST, REVIEW) 입니다.")),
    REQUIRED_EBOOK_INQUIRY_CONTENT(GeneralException("EBOOK-400-10", BAD_REQUEST, "문의 내용이 반드시 필요합니다.")),
    REQUIRED_EBOOK_INQUIRY_ID(GeneralException("EBOOK-400-11", BAD_REQUEST, "문의 식별자가 반드시 필요합니다.")),
    INVALID_EBOOK_INQUIRY_ID(GeneralException("EBOOK-400-12", BAD_REQUEST, "잘못된 형식의 문의 식별자입니다.")),
    REQUIRED_EBOOK_INQUIRY_COMMENT_CONTENT(GeneralException("EBOOK-400-13", BAD_REQUEST, "내용이 반드시 필요합니다.")),
    REQUIRED_EBOOK_INQUIRY_COMMENT_ID(GeneralException("EBOOK-400-14", BAD_REQUEST, "댓글 식별자가 반드시 필요합니다.")),
    INVALID_EBOOK_INQUIRY_COMMENT_ID(GeneralException("EBOOK-400-15", BAD_REQUEST, "잘못된 형식의 댓글 식별자입니다.")),
    INVALID_EBOOK_ID(GeneralException("EBOOK-400-16", BAD_REQUEST, "잘못된 형식의 전자책 식별자입니다.")),
    REQUIRED_EBOOK_FOR_MODIFY(GeneralException("EBOOK-400-17", BAD_REQUEST, "전자책이 반드시 필요합니다.")),
    REQUIRED_MAIN_IMAGE_ID(GeneralException("EBOOK-400-19", BAD_REQUEST, "메인 사진 식별자가 반드시 필요합니다.")),
    INVALID_MAIN_IMAGE_ID(GeneralException("EBOOK-400-20", BAD_REQUEST, "잘못된 형식의 메인 사진 식별자입니다.")),
    REQUIRED_DESCRIPTION_IMAGE_ID(GeneralException("EBOOK-400-21", BAD_REQUEST, "설명 사진 식별자가 반드시 필요합니다.")),
    INVALID_DESCRIPTION_IMAGE_ID(GeneralException("EBOOK-400-22", BAD_REQUEST, "잘못된 형식의 설명 사진 식별자입니다.")),
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
