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
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "서비스 문의")
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
                description = "- SERVICE-400-1: 서비스 문의 제목이 반드시 필요합니다.\n" +
                        "- SERVICE-400-2: 서비스 문의 내용이 반드시 필요합니다.\n" +
                        "- SERVICE-400-3: 잘못된 형식의 사진 식별자입니다.",
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
        @Schema(description = "액세스 토큰", required = true)
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
                description =
                "- COMMON-400-1 : 페이지는 1부터 조회할 수 있습니다.\n" +
                        "- COMMON-400-2 : 개수는 1~1000 까지 조회할 수 있습니다.",
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
        @Schema(description = "페이지", required = true)
        page: String,
        @Schema(description = "개수", required = true)
        count: String,
        @Schema(description = "액세스 토큰", required = true)
        authentication: String,
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
                description =
                "- SERVICE-400-5: 서비스 문의 식별자가 반드시 필요합니다.\n" +
                        "- SERVICE-400-6: 잘못된 형식의 서비스 문의 식별자입니다.\n" +
                        "- SERVICE-400-1: 서비스 문의 제목이 반드시 필요합니다. (제목이 null이 아니면서 비어 있을 경우)\n" +
                        "- SERVICE-400-2: 서비스 문의 내용이 반드시 필요합니다. (내용이 null이 아니면서 비어 있을 경우)\n" +
                        "- SERVICE-400-3: 잘못된 형식의 사진 식별자입니다. (사진 목록이 null이 아니면서 UUID가 아닐 경우)",
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
        @Schema(description = "서비스 문의 식별자", required = true)
        serviceInquiryId: String,
        request: ModifyServiceInquiryRequest,
        @Schema(description = "액세스 토큰", required = true)
        authorization: String,
    ): ModifyServiceInquiryResponse
}
