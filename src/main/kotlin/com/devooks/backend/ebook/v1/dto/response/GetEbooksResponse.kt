package com.devooks.backend.ebook.v1.dto.response

import com.devooks.backend.ebook.v1.dto.EbookView

data class GetEbooksResponse(
    val ebookList: List<EbookView>,
) {
    companion object {
        fun List<EbookView>.toGetEbooksResponse() =
            GetEbooksResponse(this)
    }
}
