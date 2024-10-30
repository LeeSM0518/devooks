package com.devooks.backend.ebook.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentDto
import com.devooks.backend.ebook.v1.dto.EbookInquiryDto
import com.devooks.backend.ebook.v1.dto.request.CreateEbookInquiryCommentRequest
import com.devooks.backend.ebook.v1.dto.request.CreateEbookInquiryRequest
import com.devooks.backend.ebook.v1.dto.request.CreateEbookRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookInquiryCommentRequest
import com.devooks.backend.ebook.v1.dto.request.SaveDescriptionImagesRequest
import com.devooks.backend.ebook.v1.dto.request.SaveMainImageRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.CreateEbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetEbookInquiryCommentsResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.SaveDescriptionImagesResponse
import com.devooks.backend.ebook.v1.dto.response.SaveMainImageResponse
import com.devooks.backend.ebook.v1.repository.EbookImageRepository
import com.devooks.backend.ebook.v1.repository.EbookInquiryCommentRepository
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
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.fileSize
import kotlinx.coroutines.delay
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
internal class EbookInquiryCommentControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
    private val ebookRepository: EbookRepository,
    private val ebookImageRepository: EbookImageRepository,
    private val ebookInquiryRepository: EbookInquiryRepository,
    private val ebookInquiryCommentRepository: EbookInquiryCommentRepository,
    private val notificationRepository: NotificationRepository,
    private val categoryRepository: CategoryRepository,
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
        ebookInquiryCommentRepository.deleteAll()
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
    fun `전자책 문의 댓글을 작성할 수 있다`(): Unit = runBlocking {
        val (accessToken, ebookInquiry) = postCreateEbookInquiry()

        val createEbookInquiryCommentRequest = CreateEbookInquiryCommentRequest(
            inquiryId = ebookInquiry.id.toString(),
            content = "content"
        )

        val ebookInquiryComment = webTestClient
            .post()
            .uri("/api/v1/ebook-inquiry-comments")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(createEbookInquiryCommentRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateEbookInquiryCommentResponse>()
            .returnResult()
            .responseBody!!
            .comment

        assertThat(ebookInquiryComment.inquiryId.toString()).isEqualTo(createEbookInquiryCommentRequest.inquiryId)
        assertThat(ebookInquiryComment.content).isEqualTo(createEbookInquiryCommentRequest.content)
        assertThat(ebookInquiryComment.writerMemberId).isEqualTo(expectedMember1.id)

        delay(100)
        val notification =
            notificationRepository.findAll().toList().find { it.type == NotificationType.INQUIRY_COMMENT }!!
        assertThat(notification.type).isEqualTo(NotificationType.INQUIRY_COMMENT)
        assertThat(notification.receiverId).isEqualTo(ebookInquiry.writerMemberId)
        assertThat(notification.note["ebookId"]).isEqualTo(ebookInquiry.ebookId.toString())
        assertThat(notification.note["receiverId"]).isEqualTo(ebookInquiry.writerMemberId.toString())
        assertThat(notification.note["commenterName"]).isEqualTo(expectedMember1.nickname)
        assertThat(notification.note["ebookInquiryId"]).isEqualTo(ebookInquiry.id.toString())
        assertThat(notification.note["ebookInquiryCommentId"]).isEqualTo(ebookInquiryComment.id.toString())

    }

    @Test
    fun `전자책 문의 댓글을 조회할 수 있다`(): Unit = runBlocking {
        val ebookInquiryComment = postCreateEbookInquiryComment()

        val foundEbookInquiryComment = webTestClient
            .get()
            .uri("/api/v1/ebook-inquiry-comments?inquiryId=${ebookInquiryComment.inquiryId}&page=1&count=10")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetEbookInquiryCommentsResponse>()
            .returnResult()
            .responseBody!!
            .comments[0]

        assertThat(foundEbookInquiryComment.id).isEqualTo(ebookInquiryComment.id)
        assertThat(foundEbookInquiryComment.content).isEqualTo(ebookInquiryComment.content)
        assertThat(foundEbookInquiryComment.inquiryId).isEqualTo(ebookInquiryComment.inquiryId)
        assertThat(foundEbookInquiryComment.writerMemberId).isEqualTo(ebookInquiryComment.writerMemberId)
        assertThat(foundEbookInquiryComment.writtenDate.toEpochMilli())
            .isEqualTo(ebookInquiryComment.writtenDate.toEpochMilli())
        assertThat(foundEbookInquiryComment.modifiedDate.toEpochMilli())
            .isEqualTo(ebookInquiryComment.modifiedDate.toEpochMilli())
    }

    @Test
    fun `전자책 문의 댓글을 수정할 수 있다`(): Unit = runBlocking {
        val ebookInquiryComment = postCreateEbookInquiryComment()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val modifyEbookInquiryCommentRequest = ModifyEbookInquiryCommentRequest(
            "content2"
        )

        val updatedComment = webTestClient
            .patch()
            .uri("/api/v1/ebook-inquiry-comments/${ebookInquiryComment.id}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(modifyEbookInquiryCommentRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyEbookInquiryCommentResponse>()
            .returnResult()
            .responseBody!!
            .comment

        assertThat(updatedComment.content).isEqualTo(modifyEbookInquiryCommentRequest.content)
    }

    @Test
    fun `전자책 문의 댓글을 삭제할 수 있다`(): Unit = runBlocking {
        val ebookInquiryComment = postCreateEbookInquiryComment()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebook-inquiry-comments/${ebookInquiryComment.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `전자책 문의 댓글 삭제시 자신이 작성한 문의가 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val ebookInquiryComment = postCreateEbookInquiryComment()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebook-inquiry-comments/${ebookInquiryComment.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `전자책 문의 댓글 수정시 댓글이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val modifyEbookInquiryCommentRequest = ModifyEbookInquiryCommentRequest(
            "content2"
        )

        webTestClient
            .patch()
            .uri("/api/v1/ebook-inquiry-comments/${UUID.randomUUID()}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(modifyEbookInquiryCommentRequest)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `전자책 문의 댓글 수정시 자신이 작성한 댓글이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val ebookInquiryComment = postCreateEbookInquiryComment()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken
        val modifyEbookInquiryCommentRequest = ModifyEbookInquiryCommentRequest(
            "content2"
        )

        webTestClient
            .patch()
            .uri("/api/v1/ebook-inquiry-comments/${ebookInquiryComment.id}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(modifyEbookInquiryCommentRequest)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `전자책 문의 댓글 작성시 문의가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (accessToken, _) = postCreateEbookInquiry()

        val createEbookInquiryCommentRequest = CreateEbookInquiryCommentRequest(
            inquiryId = UUID.randomUUID().toString(),
            content = "content"
        )

        webTestClient
            .post()
            .uri("/api/v1/ebook-inquiry-comments")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(createEbookInquiryCommentRequest)
            .exchange()
            .expectStatus().isNotFound
    }

    private suspend fun EbookInquiryCommentControllerTest.postCreateEbookInquiryComment(): EbookInquiryCommentDto {
        val (accessToken, ebookInquiry) = postCreateEbookInquiry()

        val createEbookInquiryCommentRequest = CreateEbookInquiryCommentRequest(
            inquiryId = ebookInquiry.id.toString(),
            content = "content"
        )

        val ebookInquiryComment = webTestClient
            .post()
            .uri("/api/v1/ebook-inquiry-comments")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(createEbookInquiryCommentRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateEbookInquiryCommentResponse>()
            .returnResult()
            .responseBody!!
            .comment
        return ebookInquiryComment
    }

    private suspend fun EbookInquiryCommentControllerTest.postCreateEbookInquiry(): Pair<AccessToken, EbookInquiryDto> {
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
        return Pair(accessToken, ebookInquiry)
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
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()

        val request = CreateEbookRequest(
            pdfId = pdf.id.toString(),
            title = "title",
            relatedCategoryIdList = listOf(categoryId),
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
