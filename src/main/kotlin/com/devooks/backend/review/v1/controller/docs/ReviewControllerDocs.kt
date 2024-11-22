package com.devooks.backend.review.v1.controller.docs

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.review.v1.dto.CreateReviewRequest
import com.devooks.backend.review.v1.dto.CreateReviewResponse
import com.devooks.backend.review.v1.dto.DeleteReviewResponse
import com.devooks.backend.review.v1.dto.GetReviewsResponse
import com.devooks.backend.review.v1.dto.ModifyReviewRequest
import com.devooks.backend.review.v1.dto.ModifyReviewResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "리뷰")
interface ReviewControllerDocs {

    @Operation(summary = "리뷰 작성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateReviewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- EBOOK-400-23: 전자책 식별자가 반드시 필요합니다.\n" +
                        "- EBOOK-400-16: 잘못된 형식의 전자책 식별자입니다.\n" +
                        "- REVIEW-400-1: 평점이 반드시 필요합니다.\n" +
                        "- REVIEW-400-2: 잘못된 형식의 평점입니다. (0~5점이 아닐 경우)\n" +
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
                description = "- TRANSACTION-403-1: 구매한 전자책만 리뷰가 가능합니다.",
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
            ApiResponse(
                responseCode = "409",
                description = "- REVIEW-409-1: 이미 작성한 리뷰입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun createReview(
        request: CreateReviewRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): CreateReviewResponse

    @Operation(summary = "리뷰 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetReviewsResponse::class)
                    )
                ]
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
    suspend fun getReviews(
        @Schema(description = "전자책 식별자", required = true, nullable = false)
        ebookId: String,
        @Schema(description = "페이지", required = true, nullable = false)
        page: String,
        @Schema(description = "개수", required = true, nullable = false)
        count: String,
    ): GetReviewsResponse

    @Operation(summary = "리뷰 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyReviewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- REVIEW-400-4: 리뷰 식별자가 반드시 필요합니다.\n" +
                        "- REVIEW-400-5: 잘못된 형식의 리뷰 식별자입니다.\n" +
                        "- REVIEW-400-1: 평점이 반드시 필요합니다.\n" +
                        "- REVIEW-400-2: 잘못된 형식의 평점입니다. (0~5점이 아닐 경우)\n" +
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
                description = "- REVIEW-403-1: 자신이 작성한 리뷰만 수정할 수 있습니다.",
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
            )
        ]
    )
    suspend fun modifyReview(
        @Schema(description = "리뷰 식별자", required = true, nullable = false)
        reviewId: String,
        request: ModifyReviewRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): ModifyReviewResponse

    @Operation(summary = "리뷰 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DeleteReviewResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- REVIEW-400-4: 리뷰 식별자가 반드시 필요합니다.\n" +
                        "- REVIEW-400-5: 잘못된 형식의 리뷰 식별자입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description = "- REVIEW-403-1: 자신이 작성한 리뷰만 수정할 수 있습니다.",
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
            )
        ]
    )
    suspend fun deleteReview(
        @Schema(description = "리뷰 식별자", required = true, nullable = false)
        reviewId: String,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): DeleteReviewResponse

}
