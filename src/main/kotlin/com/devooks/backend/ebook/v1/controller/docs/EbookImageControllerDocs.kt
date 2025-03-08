package com.devooks.backend.ebook.v1.controller.docs

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.ebook.v1.dto.request.SaveDescriptionImagesRequest
import com.devooks.backend.ebook.v1.dto.request.SaveMainImageRequest
import com.devooks.backend.ebook.v1.dto.response.SaveDescriptionImagesResponse
import com.devooks.backend.ebook.v1.dto.response.SaveMainImageResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "EbookImage", description = "전자책 사진")
interface EbookImageControllerDocs {

    @Operation(summary = "전자책 설명 사진 저장")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SaveDescriptionImagesResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "500",
                description = "- COMMON-500-1: 사진 저장을 실패했습니다.\n" +
                        "- COMMON-500-2: 파일 저장을 실패했습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun saveDescriptionImages(
        request: SaveDescriptionImagesRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): SaveDescriptionImagesResponse

    @Operation(summary = "전자책 메인 사진 저장")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SaveMainImageResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "500",
                description = "- COMMON-500-1: 사진 저장을 실패했습니다.\n" +
                        "- COMMON-500-2: 파일 저장을 실패했습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun saveMainImage(
        request: SaveMainImageRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): SaveMainImageResponse
}
