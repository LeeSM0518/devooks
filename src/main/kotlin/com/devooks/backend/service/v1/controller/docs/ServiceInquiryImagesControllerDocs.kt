package com.devooks.backend.service.v1.controller.docs

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.service.v1.dto.request.SaveServiceInquiryImagesRequest
import com.devooks.backend.service.v1.dto.response.SaveServiceInquiryImagesResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "서비스 문의 사진")
interface ServiceInquiryImagesControllerDocs {

    @Operation(summary = "서비스 문의 사진 저장")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SaveServiceInquiryImagesResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- COMMON-400-6: 사진이 반드시 필요합니다.",
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
    suspend fun saveServiceInquiryImages(
        request: SaveServiceInquiryImagesRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authentication: String
    ): SaveServiceInquiryImagesResponse

}
