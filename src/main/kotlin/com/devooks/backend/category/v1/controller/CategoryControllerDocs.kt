package com.devooks.backend.category.v1.controller

import com.devooks.backend.category.v1.dto.GetCategoriesResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "카테고리 API")
interface CategoryControllerDocs {

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "OK")])
    suspend fun getCategories(): GetCategoriesResponse
}
