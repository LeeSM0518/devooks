package com.devooks.backend.review.v1.controller.docs

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.review.v1.dto.CreateReviewCommentRequest
import com.devooks.backend.review.v1.dto.CreateReviewCommentResponse
import com.devooks.backend.review.v1.dto.DeleteReviewCommentResponse
import com.devooks.backend.review.v1.dto.GetReviewCommentsResponse
import com.devooks.backend.review.v1.dto.ModifyReviewCommentRequest
import com.devooks.backend.review.v1.dto.ModifyReviewCommentResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
                description = "- REVIEW-400-4: 리뷰 식별자가 반드시 필요합니다.\n" +
                        "- REVIEW-400-5: 잘못된 형식의 리뷰 식별자입니다..\n" +
                        "- REVIEW-400-3: 내용이 반드시 필요합니다.",
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
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): CreateReviewCommentResponse

    @Operation(summary = "리뷰 댓글 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetReviewCommentsResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- COMMON-400-1 : 페이지는 1부터 조회할 수 있습니다.\n" +
                        "- COMMON-400-2 : 개수는 1~1000 까지 조회할 수 있습니다.\n" +
                        "- REVIEW-400-4: 리뷰 식별자가 반드시 필요합니다.\n" +
                        "- REVIEW-400-5: 잘못된 형식의 리뷰 식별자입니다.",
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
        @Schema(description = "리뷰 식별자", required = true, nullable = false)
        reviewId: String,
        @Schema(description = "페이지", required = true, nullable = false)
        page: String,
        @Schema(description = "개수", required = true, nullable = false)
        count: String,
    ): GetReviewCommentsResponse

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
                description = "- REVIEW-400-6: 리뷰 댓글 식별자가 반드시 필요합니다.\n" +
                        "- REVIEW-400-7: 잘못된 형식의 리뷰 댓글 식별자입니다.\n" +
                        "- REVIEW-400-3: 내용이 반드시 필요합니다.",
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
        @Schema(description = "리뷰 댓글 식별자", required = true, nullable = false)
        commentId: String,
        request: ModifyReviewCommentRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
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
                description = "- REVIEW-400-6: 리뷰 댓글 식별자가 반드시 필요합니다.\n" +
                        "- REVIEW-400-7: 잘못된 형식의 리뷰 댓글 식별자입니다.",
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
        @Schema(description = "리뷰 댓글 식별자", required = true, nullable = false)
        commentId: String,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): DeleteReviewCommentResponse
}
