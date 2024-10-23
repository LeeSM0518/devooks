package com.devooks.backend.ebook.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto
import com.devooks.backend.ebook.v1.dto.EbookInquiryDto
import com.devooks.backend.ebook.v1.dto.request.CreateEbookInquiryRequest
import com.devooks.backend.ebook.v1.dto.request.CreateEbookRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookInquiryRequest
import com.devooks.backend.ebook.v1.dto.request.SaveDescriptionImagesRequest
import com.devooks.backend.ebook.v1.dto.request.SaveMainImageRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.CreateEbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetEbookInquiriesResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.SaveDescriptionImagesResponse
import com.devooks.backend.ebook.v1.dto.response.SaveMainImageResponse
import com.devooks.backend.ebook.v1.repository.EbookImageRepository
import com.devooks.backend.ebook.v1.repository.EbookInquiryRepository
import com.devooks.backend.ebook.v1.repository.EbookRepository
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.MemberRepository
import com.devooks.backend.notification.v1.adapter.out.persistence.NotificationRepository
import com.devooks.backend.notification.v1.domain.NotificationType
import com.devooks.backend.pdf.v1.dto.PdfDto
import com.devooks.backend.pdf.v1.dto.UploadPdfResponse
import com.devooks.backend.pdf.v1.repository.PdfRepository
import com.devooks.backend.pdf.v1.repository.PreviewImageRepository
import io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.fileSize
import kotlinx.coroutines.flow.toList
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
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.MULTIPART_FORM_DATA
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters

@IntegrationTest
internal class EbookInquiryControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
    private val ebookRepository: EbookRepository,
    private val ebookImageRepository: EbookImageRepository,
    private val ebookInquiryRepository: EbookInquiryRepository,
    private val notificationRepository: NotificationRepository,
) {
    lateinit var expectedMember1: Member
    lateinit var expectedMember2: Member

    @BeforeEach
    fun setup(): Unit = runBlocking {
        expectedMember1 = memberRepository.save(MemberEntity(nickname = "nickname1")).toDomain()
        expectedMember2 = memberRepository.save(MemberEntity(nickname = "nickname2")).toDomain()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        ebookInquiryRepository.deleteAll()
        ebookImageRepository.deleteAll()
        previewImageRepository.deleteAll()
        ebookRepository.deleteAll()
        pdfRepository.deleteAll()
        memberRepository.deleteAll()
        notificationRepository.deleteAll()
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
    fun `전자책 문의를 작성할 수 있다`(): Unit = runBlocking {
        val (_, createEbookResponse) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val createEbookInquiryRequest = CreateEbookInquiryRequest(
            ebookId = createEbookResponse.ebook.id.toString(),
            content = "content"
        )

        val ebookInquiry = webTestClient
            .post()
            .uri("/api/v1/ebook-inquiries")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(createEbookInquiryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateEbookInquiryResponse>()
            .returnResult()
            .responseBody!!
            .ebookInquiry

        assertThat(ebookInquiry.ebookId.toString()).isEqualTo(createEbookInquiryRequest.ebookId)
        assertThat(ebookInquiry.content).isEqualTo(createEbookInquiryRequest.content)
        assertThat(ebookInquiry.writerMemberId).isEqualTo(expectedMember1.id)

        val notification = notificationRepository.findAll().toList()[0]
        assertThat(notification.type).isEqualTo(NotificationType.INQUIRY)
        assertThat(notification.receiverId).isEqualTo(createEbookResponse.ebook.sellingMemberId)
        assertThat(notification.note["ebookId"]).isEqualTo(createEbookResponse.ebook.id.toString())
        assertThat(notification.note["ebookTitle"]).isEqualTo(createEbookResponse.ebook.title)
        assertThat(notification.note["receiverId"]).isEqualTo(createEbookResponse.ebook.sellingMemberId.toString())
        assertThat(notification.note["inquirerName"]).isEqualTo(expectedMember1.nickname)
        assertThat(notification.note["ebookInquiryId"]).isEqualTo(ebookInquiry.id.toString())
    }

    @Test
    fun `전자책 문의를 조회할 수 있다`(): Unit = runBlocking {
        val createdEbookInquiry = postCreateEbookInquiry()

        val foundEbookInquiry =
            webTestClient
                .get()
                .uri("/api/v1/ebook-inquiries?ebookId=${createdEbookInquiry.ebookId}&page=1&count=10")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<GetEbookInquiriesResponse>()
                .returnResult()
                .responseBody!!
                .ebookInquiryList[0]

        assertThat(foundEbookInquiry.id).isEqualTo(createdEbookInquiry.id)
        assertThat(foundEbookInquiry.content).isEqualTo(createdEbookInquiry.content)
        assertThat(foundEbookInquiry.ebookId).isEqualTo(createdEbookInquiry.ebookId)
        assertThat(foundEbookInquiry.writerMemberId).isEqualTo(createdEbookInquiry.writerMemberId)
        assertThat(foundEbookInquiry.writtenDate.toEpochMilli())
            .isEqualTo(createdEbookInquiry.writtenDate.toEpochMilli())
        assertThat(foundEbookInquiry.modifiedDate.toEpochMilli())
            .isEqualTo(createdEbookInquiry.modifiedDate.toEpochMilli())
    }

    @Test
    fun `전자책 문의를 수정할 수 있다`(): Unit = runBlocking {
        val createdEbookInquiry = postCreateEbookInquiry()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val request = ModifyEbookInquiryRequest("content2")

        val updatedEbookInquiry =
            webTestClient
                .patch()
                .uri("/api/v1/ebook-inquiries/${createdEbookInquiry.id}")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer $accessToken")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody<ModifyEbookInquiryResponse>()
                .returnResult()
                .responseBody!!
                .ebookInquiry

        assertThat(updatedEbookInquiry.content).isEqualTo(request.content)
    }

    @Test
    fun `전자책 문의를 삭제할 수 있다`(): Unit = runBlocking {
        val createdEbookInquiry = postCreateEbookInquiry()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebook-inquiries/${createdEbookInquiry.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `전자책 문의 삭제시 문의가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebook-inquiries/${UUID.randomUUID()}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `전자책 문의 수정시 문의가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val request = ModifyEbookInquiryRequest("content2")

        webTestClient
            .patch()
            .uri("/api/v1/ebook-inquiries/${UUID.randomUUID()}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `전자책 문의 수정시 자신이 작성한 문의가 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val createdEbookInquiry = postCreateEbookInquiry()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken
        val request = ModifyEbookInquiryRequest("content2")

        webTestClient
            .patch()
            .uri("/api/v1/ebook-inquiries/${createdEbookInquiry.id}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(FORBIDDEN.code())
    }

    @Test
    fun `전자책 문의 삭제시 자신이 작성한 문의가 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val createdEbookInquiry = postCreateEbookInquiry()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebook-inquiries/${createdEbookInquiry.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isEqualTo(FORBIDDEN.code())
    }

    @Test
    fun `전자책 문의 작성시 전자책이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val createEbookInquiryRequest = CreateEbookInquiryRequest(
            ebookId = UUID.randomUUID().toString(),
            content = "content"
        )

        webTestClient
            .post()
            .uri("/api/v1/ebook-inquiries")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(createEbookInquiryRequest)
            .exchange()
            .expectStatus().isNotFound
    }

    private suspend fun EbookInquiryControllerTest.postCreateEbookInquiry(): EbookInquiryDto {
        val (_, createEbookResponse) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val createEbookInquiryRequest = CreateEbookInquiryRequest(
            ebookId = createEbookResponse.ebook.id.toString(),
            content = "content"
        )

        val createdEbookInquiry =
            webTestClient
                .post()
                .uri("/api/v1/ebook-inquiries")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer $accessToken")
                .bodyValue(createEbookInquiryRequest)
                .exchange()
                .expectStatus().isOk
                .expectBody<CreateEbookInquiryResponse>()
                .returnResult()
                .responseBody!!
                .ebookInquiry
        return createdEbookInquiry
    }

    suspend fun postCreateEbook(): Pair<CreateEbookRequest, CreateEbookResponse> {
        val tokenGroup = tokenService.createTokenGroup(expectedMember1)
        val accessToken = tokenGroup.accessToken
        val pdf = postUploadPdfFile(accessToken)
        val imagePath = Path(javaClass.classLoader.getResource("test.jpg")!!.path)
        val imageBytes = Files.readAllBytes(imagePath)
        val imageBase64Raw = Base64.getEncoder().encodeToString(imageBytes)

        val mainImage = postSaveMainImage(imageBase64Raw, imagePath, accessToken)
        val descriptionImageList = postSaveDescriptionImages(imageBase64Raw, imagePath, accessToken)

        val request = CreateEbookRequest(
            pdfId = pdf.id.toString(),
            title = "title",
            relatedCategoryNameList = listOf("category"),
            mainImageId = mainImage.id.toString(),
            descriptionImageIdList = descriptionImageList.map { it.id.toString() },
            10000,
            "introduction",
            "tableOfContent"
        )
        val response = webTestClient
            .post()
            .uri("/api/v1/ebooks")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateEbookResponse>()
            .returnResult()
            .responseBody!!
        return Pair(request, response)
    }

    fun postSaveDescriptionImages(
        imageBase64Raw: String?,
        imagePath: Path,
        accessToken: AccessToken,
    ): List<DescriptionImageDto> {
        val saveDescriptionImagesRequest = SaveDescriptionImagesRequest(
            imageList = listOf(
                ImageDto(
                    imageBase64Raw,
                    imagePath.extension,
                    imagePath.fileSize(),
                    1
                ),
                ImageDto(
                    imageBase64Raw,
                    imagePath.extension,
                    imagePath.fileSize(),
                    2
                ),
            )
        )

        val descriptionImageList = webTestClient
            .post()
            .uri("/api/v1/ebooks/description-images")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(saveDescriptionImagesRequest)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<SaveDescriptionImagesResponse>()
            .returnResult()
            .responseBody!!
            .descriptionImageList
        return descriptionImageList
    }

    private fun postSaveMainImage(
        imageBase64Raw: String?,
        imagePath: Path,
        accessToken: AccessToken,
    ): SaveMainImageResponse.MainImageDto {
        val saveMainImageRequest = SaveMainImageRequest(
            SaveMainImageRequest.MainImageDto(
                imageBase64Raw,
                imagePath.extension,
                imagePath.fileSize(),
            )
        )

        val mainImage = webTestClient
            .post()
            .uri("/api/v1/ebooks/main-image")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(saveMainImageRequest)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<SaveMainImageResponse>()
            .returnResult()
            .responseBody!!
            .mainImage
        return mainImage
    }

    suspend fun postCreateEbookWithNoneDescriptionImageList(): Pair<CreateEbookRequest, CreateEbookResponse> {
        val tokenGroup = tokenService.createTokenGroup(expectedMember1)
        val accessToken = tokenGroup.accessToken
        val pdf = postUploadPdfFile(accessToken)
        val imagePath = Path(javaClass.classLoader.getResource("test.jpg")!!.path)
        val imageBytes = Files.readAllBytes(imagePath)
        val imageBase64Raw = Base64.getEncoder().encodeToString(imageBytes)
        val mainImage = postSaveMainImage(imageBase64Raw, imagePath, accessToken)

        val request = CreateEbookRequest(
            pdfId = pdf.id.toString(),
            title = "title",
            relatedCategoryNameList = listOf("category"),
            mainImageId = mainImage.id.toString(),
            descriptionImageIdList = null,
            10000,
            "introduction",
            "tableOfContent"
        )
        val response = webTestClient
            .post()
            .uri("/api/v1/ebooks")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateEbookResponse>()
            .returnResult()
            .responseBody!!
        return Pair(request, response)
    }


    suspend fun postUploadPdfFile(accessToken: String): PdfDto {
        val pdfResource = File(javaClass.classLoader.getResource("valid_test.pdf")!!.path)

        val formData = LinkedMultiValueMap<String, Any>()
        formData.add("pdf", FileSystemResource(pdfResource))

        val pdf = webTestClient
            .post()
            .uri("/api/v1/pdfs")
            .contentType(MULTIPART_FORM_DATA)
            .header(AUTHORIZATION, "Bearer $accessToken")
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
