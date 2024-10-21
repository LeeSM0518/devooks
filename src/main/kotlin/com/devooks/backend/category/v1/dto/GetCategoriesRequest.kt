package com.devooks.backend.category.v1.dto

import com.devooks.backend.common.dto.Paging

data class GetCategoriesRequest(
    val keyword: String,
    val paging: Paging,
) {

    constructor(
        name: String,
        page: String,
        count: String,
    ) : this(
        keyword = "%$name%",
        paging = Paging(page, count),
    )

}
