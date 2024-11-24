package com.devooks.backend.review.v1.controller.docs

import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.review.v1.dto.CreateReviewCommentRequest
import com.devooks.backend.review.v1.dto.CreateReviewCommentResponse
import com.devooks.backend.review.v1.dto.DeleteReviewCommentResponse
import com.devooks.backend.review.v1.dto.ModifyReviewCommentRequest
import com.devooks.backend.review.v1.dto.ModifyReviewCommentResponse
import com.devooks.backend.review.v1.dto.ReviewCommentView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "리뷰 댓글")
interface ReviewCommentControllerDocs {

    @Operation(summary = "리뷰 댓글 작성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateReviewCommentResponse::class)
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
                description = "- REVIEW-404-1: 존재하지 않는 리뷰입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun createReviewComment(
        request: CreateReviewCommentRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): CreateReviewCommentResponse

    @Operation(summary = "리뷰 댓글 목록 조회")
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
    suspend fun getReviewComments(
        @Schema(description = "리뷰 식별자", required = true, implementation = UUID::class)
        reviewId: UUID,
        @Schema(description = "페이지", implementation = Int::class, required = true)
        page: Int,
        @Schema(description = "개수", implementation = Int::class, required = true)
        count: Int,
    ): PageResponse<ReviewCommentView>

    @Operation(summary = "리뷰 댓글 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyReviewCommentResponse::class)
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
                description = "- REVIEW-403-2: 자신이 작성한 리뷰 댓글만 수정할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- REVIEW-404-1: 존재하지 않는 리뷰 댓글입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun modifyReviewComment(
        @Schema(description = "리뷰 댓글 식별자", required = true, implementation = UUID::class)
        commentId: UUID,
        request: ModifyReviewCommentRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): ModifyReviewCommentResponse

    @Operation(summary = "리뷰 댓글 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DeleteReviewCommentResponse::class)
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
                description = "- REVIEW-403-2: 자신이 작성한 리뷰 댓글만 수정할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- REVIEW-404-1: 존재하지 않는 리뷰 댓글입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun deleteReviewComment(
        @Schema(description = "리뷰 댓글 식별자", required = true, implementation = UUID::class)
        commentId: UUID,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): DeleteReviewCommentResponse
}
