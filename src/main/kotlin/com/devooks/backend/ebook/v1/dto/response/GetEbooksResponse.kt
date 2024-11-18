package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.repository.row.EbookRow

data class GetEbooksResponse(
    val ebookList: List<EbookRow>,
) {
    companion object {
        fun List<EbookRow>.toGetEbooksResponse() =
            GetEbooksResponse(this)
    }
}
