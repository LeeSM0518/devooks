package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.dto.EbookView
import com.devooks.backend.ebook.v1.dto.EbookView.Companion.toEbookView
import com.devooks.backend.ebook.v1.repository.row.EbookRow

data class GetEbooksResponse(
    val ebookList: List<EbookView>,
) {
    companion object {
        fun List<EbookRow>.toGetEbooksResponse() =
            GetEbooksResponse(this.map { it.toEbookView() })
    }
}
