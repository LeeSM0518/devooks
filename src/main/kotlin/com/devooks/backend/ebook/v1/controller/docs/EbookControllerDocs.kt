package com.devooks.backend.ebook.v1.controller.docs

import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.ebook.v1.domain.EbookOrder
import com.devooks.backend.ebook.v1.dto.EbookView
import com.devooks.backend.ebook.v1.dto.request.CreateEbookRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookResponse
import com.devooks.backend.ebook.v1.dto.response.DeleteEbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetDetailOfEbookResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "전자책")
interface EbookControllerDocs {

    @Operation(summary = "전자책 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK"
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
        ]
    )
    suspend fun getEbooks(
        @Schema(description = "페이지", implementation = Int::class, required = true)
        page: Int,
        @Schema(description = "개수", implementation = Int::class, required = true)
        count: Int,
        @Schema(description = "검색할 전자책 제목", nullable = true)
        title: String?,
        @Schema(description = "검색할 판매자 식별자", implementation = UUID::class, nullable = true)
        sellerMemberId: UUID?,
        @Schema(description = "검색할 전자책 식별자 목록", format = "uuid", nullable = true)
        ebookIdList: List<UUID>?,
        @Schema(description = "검색할 카테고리 식별자 목록", format = "uuid", nullable = true)
        categoryIdList: List<UUID>?,
        @Schema(description = "정렬할 속성", implementation = EbookOrder::class, nullable = true)
        orderBy: EbookOrder?,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", nullable = true)
        authorization: String?,
    ): PageResponse<EbookView>

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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "전자책 식별자", implementation = UUID::class, required = true)
        ebookId: UUID,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", nullable = true)
        authorization: String?,
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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "전자책 식별자", implementation = UUID::class, required = true)
        ebookId: UUID,
        request: ModifyEbookRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "전자책 식별자", implementation = UUID::class, required = true)
        ebookId: UUID,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): DeleteEbookResponse
}
