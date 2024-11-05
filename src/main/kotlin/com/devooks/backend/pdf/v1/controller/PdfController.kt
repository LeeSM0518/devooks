package com.devooks.backend.pdf.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.pdf.v1.domain.Pdf
import com.devooks.backend.pdf.v1.domain.PreviewImage
import com.devooks.backend.pdf.v1.dto.GetPreviewImageListResponse
import com.devooks.backend.pdf.v1.dto.PdfDto.Companion.toDto
import com.devooks.backend.pdf.v1.dto.PreviewImageDto.Companion.toDto
import com.devooks.backend.pdf.v1.dto.UploadPdfResponse
import com.devooks.backend.pdf.v1.service.PdfService
import com.devooks.backend.pdf.v1.service.PreviewImageService
import java.util.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pdfs")
class PdfController(
    private val tokenService: TokenService,
    private val pdfService: PdfService,
    private val previewImageService: PreviewImageService,
): PdfControllerDocs {

    @Transactional
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    override suspend fun uploadPdf(
        @RequestPart("pdf")
        filePart: FilePart,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): UploadPdfResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val pdf: Pdf = pdfService.save(filePart, requesterId)
        val previewImageList: List<PreviewImage> = previewImageService.save(pdf)
        return UploadPdfResponse(pdf.toDto(previewImageList))
    }

    @GetMapping("/{pdfId}/preview")
    override suspend fun getPreviewImageList(
        @PathVariable
        pdfId: UUID,
    ): GetPreviewImageListResponse {
        val pdf: Pdf = pdfService.findBy(pdfId)
        val previewImageList: List<PreviewImage> = previewImageService.findBy(pdf)
        return GetPreviewImageListResponse(previewImageList.map { it.toDto() }.sortedBy { it.previewOrder })
    }
}
