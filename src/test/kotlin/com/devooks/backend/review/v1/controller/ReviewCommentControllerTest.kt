package com.devooks.backend.review.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.common.domain.ImageExtension
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.ebook.v1.dto.EbookImageDto
import com.devooks.backend.ebook.v1.dto.request.CreateEbookRequest
import com.devooks.backend.ebook.v1.dto.request.SaveDescriptionImagesRequest
import com.devooks.backend.ebook.v1.dto.request.SaveMainImageRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookResponse
import com.devooks.backend.ebook.v1.dto.response.SaveDescriptionImagesResponse
import com.devooks.backend.ebook.v1.dto.response.SaveMainImageResponse
import com.devooks.backend.ebook.v1.repository.EbookImageRepository
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
import com.devooks.backend.review.v1.dto.CreateReviewCommentRequest
import com.devooks.backend.review.v1.dto.CreateReviewCommentResponse
import com.devooks.backend.review.v1.dto.CreateReviewRequest
import com.devooks.backend.review.v1.dto.CreateReviewResponse
import com.devooks.backend.review.v1.dto.ModifyReviewCommentRequest
import com.devooks.backend.review.v1.dto.ModifyReviewCommentResponse
import com.devooks.backend.review.v1.dto.ReviewCommentView
import com.devooks.backend.review.v1.dto.ReviewView
import com.devooks.backend.review.v1.repository.ReviewCommentRepository
import com.devooks.backend.review.v1.repository.ReviewRepository
import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import com.devooks.backend.transaciton.v1.dto.CreateTransactionRequest
import com.devooks.backend.transaciton.v1.dto.CreateTransactionResponse
import com.devooks.backend.transaciton.v1.repository.TransactionCrudRepository
import io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN
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
internal class ReviewCommentControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
    private val ebookRepository: EbookRepository,
    private val ebookImageRepository: EbookImageRepository,
    private val transactionCrudRepository: TransactionCrudRepository,
    private val reviewRepository: ReviewRepository,
    private val reviewCommentRepository: ReviewCommentRepository,
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
        reviewCommentRepository.deleteAll()
        reviewRepository.deleteAll()
        transactionCrudRepository.deleteAll()
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
    fun `리뷰 댓글을 작성할 수 있다`(): Unit = runBlocking {
        val review = postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken
        val request = CreateReviewCommentRequest(review.id, review.content)

        val reviewComment = webTestClient
            .post()
            .uri("/api/v1/review-comments")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateReviewCommentResponse>()
            .returnResult()
            .responseBody!!
            .reviewComment

        assertThat(reviewComment.reviewId).isEqualTo(request.reviewId)
        assertThat(reviewComment.content).isEqualTo(request.content)

        delay(100)
        val notification = notificationRepository.findAll().toList()
            .find { it.receiverId == review.writer.memberId }!!
        assertThat(notification.type).isEqualTo(NotificationType.REVIEW_COMMENT)
        assertThat(notification.receiverId).isEqualTo(review.writer.memberId)
        assertThat(notification.note["ebookId"]).isEqualTo(review.ebookId.toString())
        assertThat(notification.note["reviewId"]).isEqualTo(review.id.toString())
        assertThat(notification.note["receiverId"]).isEqualTo(review.writer.memberId.toString())
        assertThat(notification.note["commenterName"]).isEqualTo(expectedMember2.nickname)
    }

    @Test
    fun `리뷰 댓글 작성시 리뷰가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val review = postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken
        val request = CreateReviewCommentRequest(UUID.randomUUID(), review.content)

        webTestClient
            .post()
            .uri("/api/v1/review-comments")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `리뷰 댓글을 조회할 수 있다`(): Unit = runBlocking {
        val (request, response) = postCreateReviewComment()

        assertThat(response.id).isEqualTo(request.id)
        assertThat(response.content).isEqualTo(request.content)
        assertThat(response.reviewId).isEqualTo(request.reviewId)
        assertThat(response.writtenDate.toEpochMilli()).isEqualTo(request.writtenDate.toEpochMilli())
        assertThat(response.modifiedDate.toEpochMilli()).isEqualTo(request.modifiedDate.toEpochMilli())
        assertThat(response.writerMemberId).isEqualTo(request.writerMemberId)
    }

    @Test
    fun `리뷰 댓글을 수정할 수 있다`(): Unit = runBlocking {
        val (_, createReviewCommentResponse) = postCreateReviewComment()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        val request = ModifyReviewCommentRequest("content")

        val modifyReviewCommentResponse = webTestClient
            .patch()
            .uri("/api/v1/review-comments/${createReviewCommentResponse.id}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyReviewCommentResponse>()
            .returnResult()
            .responseBody!!
            .reviewComment

        assertThat(modifyReviewCommentResponse.reviewId).isEqualTo(createReviewCommentResponse.reviewId)
        assertThat(modifyReviewCommentResponse.content).isEqualTo(createReviewCommentResponse.content)
    }

    @Test
    fun `리뷰 댓글을 삭제할 수 있다`(): Unit = runBlocking {
        val (_, createReviewCommentResponse) = postCreateReviewComment()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/review-comments/${createReviewCommentResponse.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `리뷰 댓글 삭제시 자신이 작성한 댓글이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, createReviewCommentResponse) = postCreateReviewComment()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/review-comments/${createReviewCommentResponse.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `리뷰 댓글 수정시 자신이 작성한 댓글이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, createReviewCommentResponse) = postCreateReviewComment()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        val request = ModifyReviewCommentRequest("content")

        webTestClient
            .patch()
            .uri("/api/v1/review-comments/${createReviewCommentResponse.id}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(FORBIDDEN.code())
    }

    @Test
    fun `리뷰 댓글 작성시 댓글이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        val request = ModifyReviewCommentRequest("content")

        webTestClient
            .patch()
            .uri("/api/v1/review-comments/${UUID.randomUUID()}")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    private suspend fun postCreateReviewComment(): Pair<ReviewCommentView, ReviewCommentView> {
        val review = postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken
        val createReviewCommentRequest = CreateReviewCommentRequest(review.id, review.content)

        val reviewComment = webTestClient
            .post()
            .uri("/api/v1/review-comments")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .bodyValue(createReviewCommentRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateReviewCommentResponse>()
            .returnResult()
            .responseBody!!
            .reviewComment

        val response = webTestClient
            .get()
            .uri("/api/v1/review-comments?reviewId=${review.id}&page=1&count=10")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<ReviewCommentView>>()
            .returnResult()
            .responseBody!!

        assertThat(response.pageable.totalElements).isEqualTo(1)
        assertThat(response.pageable.totalPages).isEqualTo(1)
        return Pair(reviewComment, response.data[0])
    }

    private suspend fun postCreateReview(): ReviewView {
        val (createEbookResponse, accessToken) = postCreateEbookAndCreateTransaction()

        val createReviewRequest =
            CreateReviewRequest(
                ebookId = createEbookResponse.ebook.id,
                rating = 5,
                content = "content"
            )
        val createReviewResponse = webTestClient
            .post()
            .uri("/api/v1/reviews")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createReviewRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateReviewResponse>()
            .returnResult()
            .responseBody!!
        return createReviewResponse.review
    }

    private suspend fun postCreateEbookAndCreateTransaction(): Pair<CreateEbookResponse, AccessToken> {
        val (_, createEbookResponse) = postCreateEbook()
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = createEbookResponse.ebook.id,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            price = createEbookResponse.ebook.price
        )

        val tokenGroup = tokenService.createTokenGroup(expectedMember2)
        val accessToken = tokenGroup.accessToken

        webTestClient
            .post()
            .uri("/api/v1/transactions")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createTransactionRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateTransactionResponse>()
            .returnResult()
            .responseBody!!
        return Pair(createEbookResponse, accessToken)
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

        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val request = CreateEbookRequest(
            pdfId = pdf.id,
            title = "title",
            relatedCategoryIdList = listOf(categoryId),
            mainImageId = mainImage.id,
            descriptionImageIdList = descriptionImageList.map { it.id },
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
        imageBase64Raw: String,
        imagePath: Path,
        accessToken: AccessToken,
    ): List<EbookImageDto> {
        val saveDescriptionImagesRequest = SaveDescriptionImagesRequest(
            imageList = listOf(
                ImageDto(
                    imageBase64Raw,
                    ImageExtension.valueOf(imagePath.extension.uppercase()),
                    imagePath.fileSize().toInt(),
                ),
                ImageDto(
                    imageBase64Raw,
                    ImageExtension.valueOf(imagePath.extension.uppercase()),
                    imagePath.fileSize().toInt(),
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
        imageBase64Raw: String,
        imagePath: Path,
        accessToken: AccessToken,
    ): EbookImageDto {
        val saveMainImageRequest = SaveMainImageRequest(
            ImageDto(
                imageBase64Raw,
                ImageExtension.valueOf(imagePath.extension.uppercase()),
                imagePath.fileSize().toInt(),
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
