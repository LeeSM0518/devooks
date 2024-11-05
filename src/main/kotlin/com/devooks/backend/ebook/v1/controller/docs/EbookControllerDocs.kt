package com.devooks.backend.ebook.v1.controller.docs

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.ebook.v1.dto.request.CreateEbookRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookResponse
import com.devooks.backend.ebook.v1.dto.response.DeleteEbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetDetailOfEbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetEbooksResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "전자책")
interface EbookControllerDocs {

    @Operation(summary = "전자책 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetEbooksResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- COMMON-400-1 : 페이지는 1부터 조회할 수 있습니다.\n" +
                        "- COMMON-400-2 : 개수는 1~1000 까지 조회할 수 있습니다.\n" +
                        "- EBOOK-400-9 : 잘못된 형식의 EbookOrder(ex. LATEST, REVIEW) 입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun getEbooks(
        @Schema(description = "페이지", required = true, nullable = false)
        page: String,
        @Schema(description = "개수", required = true, nullable = false)
        count: String,
        @Schema(description = "검색할 전자책 제목", required = false, nullable = true)
        title: String,
        @Schema(description = "검색할 판매자 식별자", required = false, nullable = true)
        sellingMemberId: String,
        @Schema(description = "검색할 전자책 식별자", required = false, nullable = true)
        ebookIdList: List<String>,
        @Schema(description = "검색할 카테고리 식별자 목록", required = false, nullable = true)
        categoryIdList: List<String>,
        @Schema(description = "정렬할 속성 (ex. LATEST, REVIEW)", required = false, nullable = true)
        orderBy: String,
        @Schema(description = "액세스 토큰", required = false, nullable = true)
        authorization: String,
    ): GetEbooksResponse

    @Operation(summary = "전자책 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetDetailOfEbookResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- EBOOK-400-24 : 전자책 식별자가 반드시 필요합니다.\n" +
                        "- EBOOK-400-16 : 잘못된 형식의 전자책 식별자입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- EBOOK-404-1 : 전자책을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun getDetailOfEbook(
        @Schema(description = "전자책 식별자", required = true, nullable = false)
        ebookId: String,
        @Schema(description = "액세스 토큰", required = false, nullable = true)
        authorization: String,
    ): GetDetailOfEbookResponse

    @Operation(summary = "전자책 등록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateEbookResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- EBOOK-400-1 : PDF 식별자가 존재하지 않을 경우\n" +
                        "- EBOOK-400-2 : PDF 식별자가 UUID가 아닐 경우\n" +
                        "- EBOOK-400-3 : 전자책 제목이 비어있을 경우\n" +
                        "- EBOOK-400-4 : 관련 카테고리가 비어있을 경우\n" +
                        "- CATEGORY-400-1 : 카테고리 식별자가 UUID가 아닐 경우\n" +
                        "- EBOOK-400-20 : 메인 사진 식별자가 존재하지 않을 경우\n" +
                        "- EBOOK-400-21 : 메인 사진 식별자가 UUID가 아닐 경우\n" +
                        "- EBOOK-400-22 : 설명 사진 식별자가 존재하지 않을 경우\n" +
                        "- EBOOK-400-23 : 설명 사진 식별자가 UUID가 아닐 경우\n" +
                        "- EBOOK-400-5 : 가격이 0 ~ 9,999,999원이 아닐 경우\n" +
                        "- EBOOK-400-6 : 전자책 소개가 비어있을 경우\n" +
                        "- EBOOK-400-7 : 목차가 비어있을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- PDF-403-1 : 다른 회원이 등록한 PDF를 올릴 경우\n" +
                        "- EBOOK-403-5 : 자신이 등록한 사진이 아닐 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- PDF-404-1 : PDF가 존재하지 않을 경우\n" +
                        "- CATEGORY-404-1 : 카테고리가 존재하지 않을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun createEbook(
        request: CreateEbookRequest,
        authorization: String,
    ): CreateEbookResponse

    @Operation(summary = "전자책 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyEbookResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- EBOOK-400-23 : 전자책 식별자가 존재하지 않을 경우\n" +
                        "- EBOOK-400-16 : 전자책 식별자가 UUID가 아닐 경우\n" +
                        "- EBOOK-400-3 : 전자책 제목이 null이 아니며 비어있을 경우\n" +
                        "- EBOOK-400-4 : 관련 카테고리가 null이 아니며 비어있을 경우\n" +
                        "- CATEGORY-400-1 : 관련 카테고리가 null이 아니며 카테고리 식별자가 UUID가 아닐 경우\n" +
                        "- EBOOK-400-20 : 메인 사진 식별자가 null이 아니며 존재하지 않을 경우\n" +
                        "- EBOOK-400-21 : 메인 사진 식별자가 null이 아니며 UUID가 아닐 경우\n" +
                        "- EBOOK-400-22 : 설명 사진 식별자가 null이 아니며 존재하지 않을 경우\n" +
                        "- EBOOK-400-23 : 설명 사진 식별자가 null이 아니며 UUID가 아닐 경우\n" +
                        "- EBOOK-400-5 : 가격이 null이 아니며 0 ~ 9,999,999원이 아닐 경우\n" +
                        "- EBOOK-400-6 : 전자책 소개가 null이 아니며 비어있을 경우\n" +
                        "- EBOOK-400-7 : 목차가 null이 아니며 비어있을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- EBOOK-403-4 : 자신이 등록한 책이 아닐 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- EBOOK-404-1 : 전자책이 존재하지 않을 경우\n" +
                        "- EBOOK-404-4 : 메인 사진이 존재하지 않을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun modifyEbook(
        @Schema(description = "전자책 식별자", required = true, nullable = false)
        ebookId: String,
        request: ModifyEbookRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): ModifyEbookResponse

    @Operation(summary = "전자책 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DeleteEbookResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- EBOOK-400-23 : 전자책 식별자가 존재하지 않을 경우\n" +
                        "- EBOOK-400-16 : 전자책 식별자가 UUID가 아닐 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- EBOOK-403-6 : 자신이 등록한 책이 아닐 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- EBOOK-404-1 : 전자책이 존재하지 않을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun deleteEbook(
        @Schema(description = "전자책 식별자", required = true, nullable = false)
        ebookId: String,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): DeleteEbookResponse
}
