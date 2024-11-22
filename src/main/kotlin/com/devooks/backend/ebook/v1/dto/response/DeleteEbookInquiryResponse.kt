package com.devooks.backend.ebook.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class DeleteEbookInquiryResponse(
    @Schema(description = "결과 메시지", example = "문의 삭제를 완료했습니다.")
    val message: String = "문의 삭제를 완료했습니다."
)
