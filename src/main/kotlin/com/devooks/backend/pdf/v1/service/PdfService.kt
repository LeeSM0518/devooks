package com.devooks.backend.pdf.v1.service

import com.devooks.backend.ebook.v1.dto.command.CreateEbookCommand
import com.devooks.backend.pdf.v1.domain.Pdf
import com.devooks.backend.pdf.v1.domain.PdfInfo
import com.devooks.backend.pdf.v1.entity.PdfEntity.Companion.toEntity
import com.devooks.backend.pdf.v1.error.PdfError
import com.devooks.backend.pdf.v1.repository.PdfRepository
import java.util.*
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class PdfService(
    private val pdfResolver: PdfResolver,
    private val pdfRepository: PdfRepository,
) {

    suspend fun save(filePart: FilePart, requesterId: UUID): Pdf {
        val pdfInfo: PdfInfo = pdfResolver.savePdf(filePart)
        val pdfEntity = pdfInfo.toEntity(requesterId)
        val savedPdf = pdfRepository.save(pdfEntity)
        return savedPdf.toDomain()
    }

    suspend fun findBy(pdfId: UUID): Pdf =
        pdfRepository
            .findById(pdfId)
            ?.toDomain()
            ?: throw PdfError.NOT_FOUND_PDF.exception

    suspend fun validate(command: CreateEbookCommand) {
        val pdf = findBy(command.pdfId)
        if (pdf.uploadMemberId != command.sellingMemberId) {
            throw PdfError.FORBIDDEN_CREATE_EBOOK.exception
        }
    }

}
