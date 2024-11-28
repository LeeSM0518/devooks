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
import com.devooks.backend.review.v1.dto.CreateReviewRequest
import com.devooks.backend.review.v1.dto.CreateReviewResponse
import com.devooks.backend.review.v1.dto.ModifyReviewRequest
import com.devooks.backend.review.v1.dto.ModifyReviewResponse
import com.devooks.backend.review.v1.dto.ReviewView
import com.devooks.backend.review.v1.repository.ReviewRepository
import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import com.devooks.backend.transaciton.v1.dto.CreateTransactionRequest
import com.devooks.backend.transaciton.v1.dto.CreateTransactionResponse
import com.devooks.backend.transaciton.v1.repository.TransactionCrudRepository
import io.netty.handler.codec.http.HttpResponseStatus.CONFLICT
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
internal class ReviewControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
    private val ebookRepository: EbookRepository,
    private val ebookImageRepository: EbookImageRepository,
    private val transactionCrudRepository: TransactionCrudRepository,
    private val reviewRepository: ReviewRepository,
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
    fun `리뷰를 작성할 수 있다`(): Unit = runBlocking {
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

        val review = createReviewResponse.review
        assertThat(review.rating).isEqualTo(createReviewRequest.rating)
        assertThat(review.content).isEqualTo(createReviewRequest.content)
        assertThat(review.ebookId).isEqualTo(createReviewRequest.ebookId)
        assertThat(review.writer.memberId).isEqualTo(expectedMember2.id)

        delay(100)
        val notification = notificationRepository.findAll().toList()[0]
        assertThat(notification.type).isEqualTo(NotificationType.REVIEW)
        assertThat(notification.receiverId).isEqualTo(createEbookResponse.ebook.sellingMemberId)
        assertThat(notification.note["ebookId"]).isEqualTo(createEbookResponse.ebook.id.toString())
        assertThat(notification.note["reviewId"]).isEqualTo(review.id.toString())
        assertThat(notification.note["ebookTitle"]).isEqualTo(createEbookResponse.ebook.title)
        assertThat(notification.note["reviewerName"]).isEqualTo(expectedMember2.nickname)
    }

    @Test
    fun `전자책에 대한 리뷰를 조회할 수 있다`(): Unit = runBlocking {
        val createReviewResponse = postCreateReview()

        val getReviewsResponse = webTestClient
            .get()
            .uri("/api/v1/reviews?page=1&count=10&ebookId=${createReviewResponse.ebookId}")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<ReviewView>>()
            .returnResult()
            .responseBody!!

        val review = getReviewsResponse.data[0]
        assertThat(getReviewsResponse.pageable.totalPages).isEqualTo(1)
        assertThat(getReviewsResponse.pageable.totalElements).isEqualTo(1)
        assertThat(review.id).isEqualTo(createReviewResponse.id)
        assertThat(review.ebookId).isEqualTo(createReviewResponse.ebookId)
        assertThat(review.content).isEqualTo(createReviewResponse.content)
        assertThat(review.rating).isEqualTo(createReviewResponse.rating)
        assertThat(review.writer).isEqualTo(createReviewResponse.writer)
        assertThat(review.writtenDate.toEpochMilli()).isEqualTo(createReviewResponse.writtenDate.toEpochMilli())
        assertThat(review.modifiedDate.toEpochMilli()).isEqualTo(createReviewResponse.modifiedDate.toEpochMilli())
    }

    @Test
    fun `리뷰를 수정할 수 있다`(): Unit = runBlocking {
        val createReviewResponse = postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken
        val request = ModifyReviewRequest(
            rating = 5,
            content = "content"
        )

        val modifyReviewsResponse = webTestClient
            .patch()
            .uri("/api/v1/reviews/${createReviewResponse.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyReviewResponse>()
            .returnResult()
            .responseBody!!

        val review = modifyReviewsResponse.review
        assertThat(review.content).isEqualTo(request.content)
        assertThat(review.rating).isEqualTo(request.rating)
    }

    @Test
    fun `리뷰를 삭제할 수 있다`(): Unit = runBlocking {
        val createReviewResponse = postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/reviews/${createReviewResponse.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `리뷰 삭제시 자신이 작성한 리뷰가 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val createReviewResponse = postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/reviews/${createReviewResponse.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `리뷰 수정시 리뷰가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken
        val request = ModifyReviewRequest(
            rating = 5,
            content = "content"
        )

        webTestClient
            .patch()
            .uri("/api/v1/reviews/${UUID.randomUUID()}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `리뷰 수정시 리뷰가 자신이 작성한 것이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val createReviewResponse = postCreateReview()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val request = ModifyReviewRequest(
            rating = 5,
            content = "content"
        )

        webTestClient
            .patch()
            .uri("/api/v1/reviews/${createReviewResponse.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `리뷰를 작성시 평점이 0~5가 아닐경우 예외가 발생한다`(): Unit = runBlocking {
        val (createEbookResponse, accessToken) = postCreateEbookAndCreateTransaction()

        val createReviewRequest =
            CreateReviewRequest(
                ebookId = createEbookResponse.ebook.id,
                rating = 6,
                content = "content"
            )
        webTestClient
            .post()
            .uri("/api/v1/reviews")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createReviewRequest)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `구매하지 않은 책에 리뷰를 작성할 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, createEbookResponse) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        val createReviewRequest =
            CreateReviewRequest(
                ebookId = createEbookResponse.ebook.id,
                rating = 5,
                content = "content"
            )
        webTestClient
            .post()
            .uri("/api/v1/reviews")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createReviewRequest)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `존재하지 않는 책에 리뷰를 작성할 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, accessToken) = postCreateEbookAndCreateTransaction()

        val createReviewRequest =
            CreateReviewRequest(
                ebookId = UUID.randomUUID(),
                rating = 5,
                content = "content"
            )
        webTestClient
            .post()
            .uri("/api/v1/reviews")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createReviewRequest)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `리뷰를 작성시 이미 리뷰가 존재할 경우 예외가 발생한다`(): Unit = runBlocking {
        val (createEbookResponse, accessToken) = postCreateEbookAndCreateTransaction()

        val createReviewRequest =
            CreateReviewRequest(
                ebookId = createEbookResponse.ebook.id,
                rating = 5,
                content = "content"
            )
        webTestClient
            .post()
            .uri("/api/v1/reviews")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createReviewRequest)
            .exchange()
            .expectStatus().isOk

        webTestClient
            .post()
            .uri("/api/v1/reviews")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createReviewRequest)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT.code())
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
