package com.devooks.backend.common.dto

import com.devooks.backend.common.dto.PageResponse.PageableResponse.Companion.toPageable
import org.springframework.data.domain.Page

data class PageResponse<T>(
    val data: List<T>,
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
        val totalPages: Int,
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
