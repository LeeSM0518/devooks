package com.devooks.backend.ebook.v1.controller.docs

import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentView
import com.devooks.backend.ebook.v1.dto.request.CreateEbookInquiryCommentRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookInquiryCommentRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.DeleteEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryCommentResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "전자책 문의 댓글")
interface EbookInquiryCommentControllerDocs {

    @Operation(summary = "전자책 문의 댓글 작성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateEbookInquiryCommentResponse::class)
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
                description = "- EBOOK-404-2: 문의을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun createEbookInquiryComment(
        request: CreateEbookInquiryCommentRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): CreateEbookInquiryCommentResponse

    @Operation(summary = "전자책 문의 댓글 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
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
    suspend fun getEbookInquiryComments(
        @Schema(description = "전자책 문의 식별자", required = true, implementation = UUID::class)
        inquiryId: UUID,
        @Schema(description = "페이지", implementation = Int::class, required = true)
        page: Int,
        @Schema(description = "개수", implementation = Int::class, required = true)
        count: Int,
    ): PageResponse<EbookInquiryCommentView>

    @Operation(summary = "전자책 문의 댓글 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyEbookInquiryCommentResponse::class)
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
                description = "- EBOOK-403-3: 자신이 작성한 댓글만 수정할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- EBOOK-404-3: 댓글을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun modifyEbookInquiryComment(
        @Schema(description = "전자책 문의 댓글 식별자", required = true, implementation = UUID::class)
        commentId: UUID,
        request: ModifyEbookInquiryCommentRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): ModifyEbookInquiryCommentResponse

    @Operation(summary = "전자책 문의 댓글 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DeleteEbookInquiryCommentResponse::class)
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
                description = "- EBOOK-403-3: 자신이 작성한 댓글만 수정할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- EBOOK-404-3: 댓글을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun deleteEbookInquiryComment(
        @Schema(description = "전자책 문의 댓글 식별자", required = true, implementation = UUID::class)
        commentId: UUID,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): DeleteEbookInquiryCommentResponse

}
