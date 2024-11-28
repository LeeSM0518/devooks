package com.devooks.backend.pdf.v1.controller

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.pdf.v1.dto.GetPreviewImageListResponse
import com.devooks.backend.pdf.v1.dto.UploadPdfResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.codec.multipart.FilePart

@Tag(name = "PDF")
interface PdfControllerDocs {

    @Operation(summary = "PDF 파일 업로드")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = UploadPdfResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- PDF-400-1 : PDF 파일은 0GB 이상 1GB 이하만 업로드 가능합니다.\n" +
                        "- PDF-400-2 : PDF 파일은 최소 5장 이상만 업로드 가능합니다.\n" +
                        "- PDF-400-3 : 읽을 수 없는 PDF 파일입니다.\n",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "500",
                description =
                "- PDF-500-1 : PDF 파일 전송을 실패했습니다.\n" +
                        "- PDF-500-2 : 미리보기 사진 저장을 실패했습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun uploadPdf(
        @Schema(description = "PDF 파일", required = true)
        filePart: FilePart,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true)
        authorization: String,
    ): UploadPdfResponse

    @Operation(summary = "미리보기 사진 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetPreviewImageListResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- PDF-400-1 : PDF 파일은 0GB 이상 1GB 이하만 업로드 가능합니다.\n" +
                        "- PDF-400-2 : PDF 파일은 최소 5장 이상만 업로드 가능합니다.\n" +
                        "- PDF-400-3 : 읽을 수 없는 PDF 파일입니다.\n",
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
                "- PDF-404-1 : 존재하지 않는 PDF 입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "500",
                description =
                "- PDF-500-3 : 미리보기 사진 조회를 실패했습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun getPreviewImageList(
        @Schema(description = "PDF 식별자", required = true, implementation = UUID::class)
        pdfId: UUID
    ): GetPreviewImageListResponse
}
