package com.devooks.backend.service.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus

enum class ServiceInquiryError(val exception: GeneralException) {
    // 403
    FORBIDDEN_REGISTER_SERVICE_INQUIRY_TO_IMAGE(GeneralException("SERVICE-403-1", HttpStatus.FORBIDDEN, "자신이 등록한 사진만 등록할 수 있습니다.")),
    FORBIDDEN_MODIFY_SERVICE_INQUIRY(GeneralException("SERVICE-403-2", HttpStatus.FORBIDDEN, "자신이 등록한 서비스 문의만 수정할 수 있습니다.")),

    // 404
    NOT_FOUND_SERVICE_INQUIRY(GeneralException("SERVICE-404-1", HttpStatus.NOT_FOUND, "서비스 문의를 찾을 수 없습니다.")),

    // 500
    FAIL_MAP_TO_INQUIRY_PROCESSING_STATUS(GeneralException("SERVICE-500-1", HttpStatus.INTERNAL_SERVER_ERROR, "서비스 문의 조회를 실패했습니다.")),
}
