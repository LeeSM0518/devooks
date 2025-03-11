package com.devooks.backend.service.v1.controller.docs

import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.review.v1.dto.ModifyReviewCommentResponse
import com.devooks.backend.service.v1.dto.ServiceInquiryView
import com.devooks.backend.service.v1.dto.request.CreateServiceInquiryRequest
import com.devooks.backend.service.v1.dto.request.ModifyServiceInquiryRequest
import com.devooks.backend.service.v1.dto.response.CreateServiceInquiryResponse
import com.devooks.backend.service.v1.dto.response.ModifyServiceInquiryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "ServiceInquiry", description = "서비스 문의")
interface ServiceInquiryControllerDocs {

    @Operation(summary = "서비스 문의 작성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateServiceInquiryResponse::class)
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
                description = "- SERVICE-403-1: 자신이 등록한 사진만 등록할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun createServiceInquiry(
        request: CreateServiceInquiryRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): CreateServiceInquiryResponse

    @Operation(summary = "서비스 문의 목록 조회")
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
    suspend fun getServiceInquiries(
        @Schema(description = "페이지", implementation = Int::class, required = true)
        page: Int,
        @Schema(description = "개수", implementation = Int::class, required = true)
        count: Int,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): PageResponse<ServiceInquiryView>

    @Operation(summary = "서비스 문의 수정")
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
                description = "- SERVICE-403-2: 자신이 등록한 서비스 문의만 수정할 수 있습니다.\n" +
                        "- SERVICE-403-1: 자신이 등록한 사진만 등록할 수 있습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description = "- SERVICE-404-1: 서비스 문의를 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun modifyServiceInquiry(
        @Schema(description = "서비스 문의 식별자", required = true, implementation = UUID::class)
        serviceInquiryId: UUID,
        request: ModifyServiceInquiryRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): ModifyServiceInquiryResponse
}
