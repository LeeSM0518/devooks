package com.devooks.backend.pdf.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.MemberRepository
import com.devooks.backend.pdf.v1.dto.GetPreviewImageListResponse
import com.devooks.backend.pdf.v1.dto.PdfDto
import com.devooks.backend.pdf.v1.dto.UploadPdfResponse
import com.devooks.backend.pdf.v1.repository.PdfRepository
import com.devooks.backend.pdf.v1.repository.PreviewImageRepository
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.MULTIPART_FORM_DATA
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters

@IntegrationTest
internal class PdfControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
) {

    lateinit var expectedMember: Member

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        expectedMember = memberRepository.save(MemberEntity(nickname = "nickname")).toDomain()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        previewImageRepository.deleteAll()
        pdfRepository.deleteAll()
        memberRepository.deleteAll()
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpAll(): Unit = runBlocking {
            createDirectories()
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll(): Unit = runBlocking {
            File(STATIC_ROOT_PATH).deleteRecursively()
        }
    }

    @Test
    fun `PDF 파일을 저장할 수 있다`(): Unit = runBlocking {
        val pdf = postUploadPdfFile()

        val pdfPath = Path(pdf.pdfInfo.filePath.substring(1))

        assertThat(pdf.uploadMemberId).isEqualTo(expectedMember.id)
        assertThat(pdfPath.exists()).isTrue()
        assertThat(pdf.pdfInfo.pageCount).isEqualTo(9)
        pdf.previewImageList.forEach { previewImage ->
            val imagePath = Path(previewImage.imagePath.substring(1))
            assertThat(imagePath.exists()).isTrue()
            assertThat(previewImage.pdfId).isEqualTo(pdf.id)
            assertThat(previewImage.previewOrder).isNotZero()
        }
    }

    @Test
    fun `미리보기 파일을 조회할 수 있다`(): Unit = runBlocking {
        val pdf = postUploadPdfFile()

        val previewImageList = webTestClient
            .get()
            .uri("/api/v1/pdfs/${pdf.id}/preview")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetPreviewImageListResponse>()
            .returnResult()
            .responseBody!!
            .previewImageList

        previewImageList.forEach {
            assertThat(Path(it.imagePath.substring(1)).exists()).isTrue()
            assertThat(it.pdfId).isEqualTo(pdf.id)
            assertThat(it.previewOrder).isNotZero()
        }
    }

    @Test
    fun `미리보기 파일 조회시 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        webTestClient
            .get()
            .uri("/api/v1/pdfs/${UUID.randomUUID()}/preview")
            .exchange()
            .expectStatus().isNotFound
    }

    private suspend fun postUploadPdfFile(): PdfDto {
        val tokenGroup = tokenService.createTokenGroup(expectedMember)
        val pdfResource = File(javaClass.classLoader.getResource("valid_test.pdf")!!.path)

        val formData = LinkedMultiValueMap<String, Any>()
        formData.add("pdf", FileSystemResource(pdfResource))

        val pdf = webTestClient
            .post()
            .uri("/api/v1/pdfs")
            .contentType(MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, "Bearer ${tokenGroup.accessToken}")
            .body(BodyInserters.fromMultipartData(formData))
            .exchange()
            .expectStatus().isOk
            .expectBody<UploadPdfResponse>()
            .returnResult()
            .responseBody!!
            .pdf
        return pdf
    }
}
