package com.devooks.backend.pdf.v1.service

import com.devooks.backend.BackendApplication.Companion.PDF_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.PREVIEW_IMAGE_ROOT_PATH
import com.devooks.backend.common.exception.GeneralException
import com.devooks.backend.common.utils.logger
import com.devooks.backend.pdf.v1.domain.PdfInfo
import com.devooks.backend.pdf.v1.domain.PreviewImageInfo
import com.devooks.backend.pdf.v1.error.PdfError
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.fileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component

@Component
class PdfResolver {

    private val logger = logger()
    private val pdfUploadDirectory = Paths.get(PDF_ROOT_PATH)
    private val previewImageDirectory = Paths.get(PREVIEW_IMAGE_ROOT_PATH)

    suspend fun savePdf(filePart: FilePart): PdfInfo =
        runCatching {
            val pdfFilePath = savePdfFile(filePart)
            validatePdfFile(pdfFilePath)
            val numberOfPages = getNumberOfPages(pdfFilePath)
            PdfInfo(filePath = pdfFilePath, pageCount = numberOfPages)
        }.getOrElse { exception ->
            throw when (exception) {
                is GeneralException -> exception
                else -> {
                    val generalException = PdfError.FAIL_SAVE_PDF_FILE.exception
                    logger.error(generalException.message, exception)
                    generalException
                }
            }
        }

    suspend fun savePreviewImages(pdf: PdfInfo): List<PreviewImageInfo> =
        runCatching {
            val pdfFile = pdf.filePath.toFile()

            PDDocument
                .load(pdfFile)
                .use { document -> savePreviewImageFiles(document).toList() }
        }.getOrElse { exception ->
            val generalException = PdfError.FAIL_SAVE_PREVIEW_IMAGE_FILES.exception
            logger.error(generalException.message)
            logger.error(exception.stackTraceToString())
            throw generalException
        }

    private fun savePreviewImageFiles(document: PDDocument?): Flow<PreviewImageInfo> {
        val pdfRenderer = PDFRenderer(document)
        return List(3) { index -> index }
            .asFlow()
            .map { index ->
                val previewImageFile = previewImageDirectory.resolve("${UUID.randomUUID()}.jpg").toFile()
                val image = pdfRenderer.renderImageWithDPI(index, PREVIEW_IMAGE_DPI, ImageType.RGB)
                ImageIO.write(image, PREVIEW_IMAGE_EXTENSION, previewImageFile)
                PreviewImageInfo(order = index + 1, imagePath = previewImageFile.toPath())
            }
            .flowOn(Dispatchers.IO)
    }

    private fun validatePdfFile(pdfFilePath: Path) {
        val fileSize = pdfFilePath.fileSize()
        if (fileSize <= MIN_PDF_FILE_BYTE_SIZE || fileSize > MAX_PDF_FILE_BYTE_SIZE) {
            throw PdfError.INVALID_PDF_FILE_SIZE.exception
        }
    }

    private fun getNumberOfPages(pdfFilePath: Path): Int =
        runCatching {
            PDDocument.load(pdfFilePath.toFile()).use { document ->
                val numberOfPages = document.numberOfPages
                if (numberOfPages < MIN_PDF_PAGE_COUNT) {
                    throw PdfError.INVALID_PDF_FILE_PAGE_COUNT.exception
                }
                numberOfPages
            }
        }.getOrElse { exception ->
            throw when (exception) {
                is GeneralException -> exception
                else -> PdfError.UNREADABLE_PDF_FILE.exception
            }
        }

    private suspend fun savePdfFile(filePart: FilePart): Path {
        val pdfFilePath = pdfUploadDirectory.resolve("${UUID.randomUUID()}.pdf")
        withContext(Dispatchers.IO) {
            filePart.transferTo(pdfFilePath).block()
        }
        return pdfFilePath
    }

    companion object {
        const val MIN_PDF_FILE_BYTE_SIZE = 0
        const val MAX_PDF_FILE_BYTE_SIZE = 1_000_000_000
        const val MIN_PDF_PAGE_COUNT = 5
        const val PREVIEW_IMAGE_DPI = 300f
        const val PREVIEW_IMAGE_EXTENSION = "jpg"
    }
}
