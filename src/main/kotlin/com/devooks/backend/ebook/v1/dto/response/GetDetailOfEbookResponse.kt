package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.dto.EbookDetailView

data class GetDetailOfEbookResponse(
    val ebook: EbookDetailView
) {
    companion object {
        fun EbookDetailView.toGetDetailOfEbookResponse() =
            GetDetailOfEbookResponse(this)
    }
}
