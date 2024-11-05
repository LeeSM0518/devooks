package com.devooks.backend.pdf.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND

enum class PdfError(val exception: GeneralException) {
    // 400
    INVALID_PDF_FILE_SIZE(GeneralException("PDF-400-1", BAD_REQUEST, "PDF 파일은 0GB 이상 1GB 이하만 업로드 가능합니다.")),
    INVALID_PDF_FILE_PAGE_COUNT(GeneralException("PDF-400-2", BAD_REQUEST, "PDF 파일은 최소 5장 이상만 업로드 가능합니다.")),
    UNREADABLE_PDF_FILE(GeneralException("PDF-400-3", BAD_REQUEST, "읽을 수 없는 PDF 파일입니다.")),

    // 403
    FORBIDDEN_CREATE_EBOOK(GeneralException("PDF-403-1", FORBIDDEN, "읽을 수 없는 PDF 파일입니다.")),

    // 404
    NOT_FOUND_PDF(GeneralException("PDF-404-1", NOT_FOUND, "존재하지 않는 PDF 입니다.")),


    // 500
    FAIL_SAVE_PDF_FILE(GeneralException("PDF-500-1", INTERNAL_SERVER_ERROR, "PDF 파일 저장을 실패했습니다.")),
    FAIL_SAVE_PREVIEW_IMAGE_FILES(GeneralException("PDF-500-2", INTERNAL_SERVER_ERROR, "미리보기 사진 저장을 실패했습니다.")),
    FAIL_FIND_PREVIEW_IMAGE(GeneralException("PDF-500-3", INTERNAL_SERVER_ERROR, "미리보기 사진 조회를 실패했습니다.")),
}
