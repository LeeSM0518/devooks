package com.devooks.backend.common.dto

import com.devooks.backend.common.dto.PageResponse.PageableResponse.Companion.toPageable
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

data class PageResponse<T>(
    @Schema(description = "조회 데이터")
    val data: List<T>,
    @Schema(description = "페이징 정보")
    val pageable: PageableResponse,
) {
    companion object {
        fun <T> Page<T>.toResponse() =
            PageResponse(
                data = this.content,
                pageable = this.toPageable()
            )
    }

    data class PageableResponse(
        @Schema(description = "조회 가능한 페이지 수")
        val totalPages: Int,
        @Schema(description = "조회 가능한 전체 수")
        val totalElements: Long,
    ) {
        companion object {
            fun Page<*>.toPageable() =
                PageableResponse(
                    totalPages = this.totalPages,
                    totalElements = this.totalElements,
                )
        }
    }
}
