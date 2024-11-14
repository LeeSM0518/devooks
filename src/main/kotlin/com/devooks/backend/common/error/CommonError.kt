package com.devooks.backend.common.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

enum class CommonError(val exception: GeneralException) {
    // 400
    INVALID_PAGE(GeneralException("COMMON-400-1", BAD_REQUEST, "페이지는 1부터 조회할 수 있습니다.")),
    INVALID_COUNT(GeneralException("COMMON-400-2", BAD_REQUEST, "개수는 1~1000 까지 조회할 수 있습니다.")),
    REQUIRED_BASE64RAW(GeneralException("COMMON-400-3", BAD_REQUEST, "이미지 내용이 반드시 필요합니다.")),
    INVALID_IMAGE_EXTENSION(
        GeneralException(
            "COMMON-400-4",
            BAD_REQUEST,
            "유효하지 않은 이미지 확장자입니다. JPG, PNG, JPEG만 가능합니다."
        )
    ),
    INVALID_BYTE_SIZE(GeneralException("COMMON-400-5", BAD_REQUEST, "50MB 이하의 영상만 저장이 가능합니다.")),
    REQUIRED_IMAGE(GeneralException("COMMON-400-6", BAD_REQUEST, "이미지가 반드시 필요합니다.")),
    INVALID_IMAGE_ORDER(GeneralException("COMMON-400-7", BAD_REQUEST, "유효하지 않은 이미지 순서입니다.")),

    // 500
    FAIL_SAVE_IMAGE(GeneralException("COMMON-500-1", INTERNAL_SERVER_ERROR, "이미지 저장을 실패했습니다.")),
    FAIL_SAVE_FILE(GeneralException("COMMON-500-2", INTERNAL_SERVER_ERROR, "파일 저장을 실패했습니다.")),
    FAIL_CREATE_DIRECTORY(GeneralException("COMMON-500-3", INTERNAL_SERVER_ERROR, "디렉터리 저장을 실패했습니다."))
    ;

    override fun toString(): String {
        return "CommonError(exception=$exception)"
    }
}
