package com.devooks.backend.category.v1.controller

import com.devooks.backend.category.v1.dto.GetCategoriesResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "Category", description = "카테고리")
interface CategoryControllerDocs {

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetCategoriesResponse::class)
                    )
                ]
            )
        ]
    )
    suspend fun getCategories(): GetCategoriesResponse
}
