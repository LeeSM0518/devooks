package com.devooks.backend.pdf.v1.service

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.common.CustomFilePart
import com.devooks.backend.common.assertThrows
import com.devooks.backend.pdf.v1.domain.PdfInfo
import com.devooks.backend.pdf.v1.error.PdfError
import java.io.File
import kotlin.io.path.exists
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.codec.multipart.FilePart

internal class PdfResolverTest {

    private val pdfResolver = PdfResolver()
    private val validPdfFilePath = object {}.javaClass.classLoader.getResource("valid_test.pdf")!!.file
    private val invalidPdfFilePath1 = object {}.javaClass.classLoader.getResource("invalid_test1.pdf")!!.file
    private val invalidPdfFilePath2 = object {}.javaClass.classLoader.getResource("invalid_test2.pdf")!!.file

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp(): Unit = runBlocking {
            createDirectories()
        }

        @JvmStatic
        @AfterAll
        fun tearDown(): Unit = runBlocking {
            File(STATIC_ROOT_PATH).deleteRecursively()
        }
    }

    @Test
    fun `PDF 파일을 저장할 수 있다`(): Unit = runBlocking {
        val file = File(validPdfFilePath)
        val filePart: FilePart = getFilePart(file)

        val savedPdf = pdfResolver.savePdf(filePart)

        assertThat(savedPdf.filePath.exists()).isEqualTo(true)
        assertThat(savedPdf.pageCount).isEqualTo(9)
    }

    @Test
    fun `PDF 파일이 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val file = File(invalidPdfFilePath1)
        val multipartFile = getFilePart(file)

        assertThrows(PdfError.INVALID_PDF_FILE_SIZE.exception) {
            pdfResolver.savePdf(multipartFile)
        }
    }

    @Test
    fun `PDF 파일의 페이지가 5 미만일 경우 예외가 발생한다`(): Unit = runBlocking {
        val file = File(invalidPdfFilePath2)
        val multipartFile = getFilePart(file)

        assertThrows(PdfError.INVALID_PDF_FILE_PAGE_COUNT.exception) {
            pdfResolver.savePdf(multipartFile)
        }
    }

    @Test
    fun `미리보기 파일을 저장할 수 있다`(): Unit = runBlocking {
        val file = File(validPdfFilePath)
        val filePart: FilePart = getFilePart(file)

        val savedPdf: PdfInfo = pdfResolver.savePdf(filePart)

        assertThat(savedPdf.filePath.exists()).isTrue()
        assertThat(savedPdf.pageCount).isEqualTo(9)

        val images = pdfResolver.savePreviewImages(savedPdf)

        images.forEach { image ->
            assertThat(image.imagePath.exists()).isTrue()
            assertThat(image.order).isNotZero()
        }
    }

    private fun getFilePart(file: File): FilePart {
        return CustomFilePart(file)
    }
}