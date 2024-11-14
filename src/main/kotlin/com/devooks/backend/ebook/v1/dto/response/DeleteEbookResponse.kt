package com.devooks.backend.ebook.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class DeleteEbookResponse(
    @Schema(description = "결과 메시지", example = "전자책 삭제를 완료했습니다.")
    val message: String = "전자책 삭제를 완료했습니다."
)
