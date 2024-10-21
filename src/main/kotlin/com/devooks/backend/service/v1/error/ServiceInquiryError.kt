package com.devooks.backend.service.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus

enum class ServiceInquiryError(val exception: GeneralException) {
    // 400
    REQUIRED_SERVICE_INQUIRY_TITLE(GeneralException("SERVICE-400-1", HttpStatus.BAD_REQUEST, "서비스 문의 제목이 반드시 필요합니다.")),
    REQUIRED_SERVICE_INQUIRY_CONTENT(GeneralException("SERVICE-400-2", HttpStatus.BAD_REQUEST, "서비스 문의 내용이 반드시 필요합니다.")),
    INVALID_SERVICE_INQUIRY_IMAGE_ID(GeneralException("SERVICE-400-3", HttpStatus.BAD_REQUEST, "잘못된 형식의 사진 식별자입니다.")),
    REQUIRED_SERVICE_INQUIRY_FOR_MODIFY(GeneralException("SERVICE-400-4", HttpStatus.BAD_REQUEST, "서비스 문의가 반드시 필요합니다.")),
    REQUIRED_IS_CHANGED_FOR_MODIFY(GeneralException("SERVICE-400-5", HttpStatus.BAD_REQUEST, "수정 여부가 반드시 필요합니다.")),
    REQUIRED_SERVICE_INQUIRY_ID(GeneralException("SERVICE-400-6", HttpStatus.BAD_REQUEST, "서비스 문의 식별자가 반드시 필요합니다.")),
    INVALID_SERVICE_INQUIRY_ID(GeneralException("SERVICE-400-7", HttpStatus.BAD_REQUEST, "잘못된 형식의 서비스 문의 식별자입니다.")),

    // 403
    FORBIDDEN_REGISTER_SERVICE_INQUIRY_TO_IMAGE(GeneralException("SERVICE-403-1", HttpStatus.FORBIDDEN, "자신이 등록한 사진만 서비스 문의에 등록할 수 있습니다.")),
    FORBIDDEN_MODIFY_SERVICE_INQUIRY(GeneralException("SERVICE-403-2", HttpStatus.FORBIDDEN, "자신이 등록한 서비스 문의만 수정할 수 있습니다.")),

    // 404
    NOT_FOUND_SERVICE_INQUIRY(GeneralException("SERVICE-404-1", HttpStatus.NOT_FOUND, "서비스 문의를 찾을 수 없습니다.")),

    // 500
    FAIL_MAP_TO_INQUIRY_PROCESSING_STATUS(GeneralException("SERVICE-500-1", HttpStatus.INTERNAL_SERVER_ERROR, "서비스 문의 조회를 실패했습니다.")),
}