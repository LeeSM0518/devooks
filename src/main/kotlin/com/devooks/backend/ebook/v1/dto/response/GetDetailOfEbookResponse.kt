package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.dto.EbookDetailView
import com.devooks.backend.ebook.v1.dto.EbookDetailView.Companion.toEbookDetailView
import com.devooks.backend.ebook.v1.repository.row.EbookDetailRow

data class GetDetailOfEbookResponse(
    val ebook: EbookDetailView
) {
    companion object {
        fun EbookDetailRow.toGetDetailOfEbookResponse() =
            GetDetailOfEbookResponse(this.toEbookDetailView())
    }
}
