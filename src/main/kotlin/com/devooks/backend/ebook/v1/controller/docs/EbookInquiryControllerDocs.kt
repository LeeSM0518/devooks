package com.devooks.backend.ebook.v1.controller.docs

import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.ebook.v1.dto.EbookInquiryView
import com.devooks.backend.ebook.v1.dto.request.CreateEbookInquiryRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookInquiryRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.DeleteEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "전자책 문의")
interface EbookInquiryControllerDocs {

    @Operation(summary = "전자책 문의 작성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateEbookInquiryResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- EBOOK-400-23: 전자책 식별자가 반드시 필요합니다.\n" +
                        "- EBOOK-400-16: 잘못된 형식의 전자책 식별자입니다.\n" +
                        "- EBOOK-400-10: 문의 내용이 반드시 필요합니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- EBOOK-404-1: 전자책을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun createEbookInquiry(
        request: CreateEbookInquiryRequest,
        @Schema(description = "액세스 토큰", required = true)
        authorization: String,
    ): CreateEbookInquiryResponse

    @Operation(summary = "전자책 문의 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- COMMON-400-1 : 페이지는 1부터 조회할 수 있습니다.\n" +
                        "- COMMON-400-2 : 개수는 1~1000 까지 조회할 수 있습니다.\n" +
                        "- EBOOK-400-23 : 전자책 식별자가 반드시 필요합니다.\n" +
                        "- EBOOK-400-16 : 잘못된 형식의 전자책 식별자입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun getEbookInquiries(
        @Schema(description = "전자책 식별자", required = true)
        ebookId: String,
        @Schema(description = "페이지", required = true)
        page: String,
        @Schema(description = "개수", required = true)
        count: String,
    ): PageResponse<EbookInquiryView>

    @Operation(summary = "전자책 문의 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyEbookInquiryResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- EBOOK-400-11: 문의 식별자가 반드시 필요합니다.\n" +
                        "- EBOOK-400-12: 잘못된 형식의 리뷰 식별자입니다.\n" +
                        "- EBOOK-400-10: 문의 내용이 반드시 필요합니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description = "- EBOOK-403-2: 자신이 작성한 문의만 수정할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- EBOOK-404-2: 문의을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun modifyEbookInquiry(
        @Schema(description = "전자책 문의 식별자", required = true)
        inquiryId: String,
        request: ModifyEbookInquiryRequest,
        @Schema(description = "액세스 토큰", required = true)
        authorization: String,
    ): ModifyEbookInquiryResponse

    @Operation(summary = "전자책 문의 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DeleteEbookInquiryResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- EBOOK-400-11: 문의 식별자가 반드시 필요합니다.\n" +
                        "- EBOOK-400-12: 잘못된 형식의 리뷰 식별자입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description = "- EBOOK-403-2: 자신이 작성한 문의만 수정할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- EBOOK-404-2: 문의을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun deleteEbookInquiry(
        @Schema(description = "전자책 문의 식별자", required = true)
        inquiryId: String,
        @Schema(description = "액세스 토큰", required = true)
        authorization: String,
    ): DeleteEbookInquiryResponse

}
