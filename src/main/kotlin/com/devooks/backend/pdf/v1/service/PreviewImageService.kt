package com.devooks.backend.pdf.v1.service

import com.devooks.backend.pdf.v1.domain.Pdf
import com.devooks.backend.pdf.v1.domain.PreviewImage
import com.devooks.backend.pdf.v1.domain.PreviewImageInfo
import com.devooks.backend.pdf.v1.entity.PreviewImageEntity.Companion.toEntity
import com.devooks.backend.pdf.v1.error.PdfError
import com.devooks.backend.pdf.v1.repository.PreviewImageRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class PreviewImageService(
    private val pdfResolver: PdfResolver,
    private val previewImageRepository: PreviewImageRepository,
) {

    suspend fun save(pdf: Pdf): List<PreviewImage> {
        val infoList: List<PreviewImageInfo> = pdfResolver.savePreviewImages(pdf.info)
        val entityList = infoList.map { it.toEntity(pdf.id) }
        val savedEntityList = previewImageRepository.saveAll(entityList)
        return savedEntityList.map { it.toDomain() }.toList()
    }

    suspend fun findBy(pdf: Pdf): List<PreviewImage> =
        previewImageRepository
            .findAllByPdfId(pdf.id)
            .takeIf { it.isNotEmpty() }
            ?.map { it.toDomain() }
            ?: throw PdfError.FAIL_FIND_PREVIEW_IMAGE.exception
}
