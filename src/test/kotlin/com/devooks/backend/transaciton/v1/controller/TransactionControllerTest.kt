package com.devooks.backend.transaciton.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.common.dto.ImageDto
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
import com.devooks.backend.pdf.v1.dto.PdfDto
import com.devooks.backend.pdf.v1.dto.UploadPdfResponse
import com.devooks.backend.pdf.v1.repository.PdfRepository
import com.devooks.backend.pdf.v1.repository.PreviewImageRepository
import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import com.devooks.backend.transaciton.v1.dto.CreateTransactionRequest
import com.devooks.backend.transaciton.v1.dto.CreateTransactionResponse
import com.devooks.backend.transaciton.v1.dto.GetBuyHistoriesResponse
import com.devooks.backend.transaciton.v1.dto.GetSellHistoriesResponse
import com.devooks.backend.transaciton.v1.repository.TransactionCrudRepository
import io.netty.handler.codec.http.HttpResponseStatus.CONFLICT
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
internal class TransactionControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
    private val ebookRepository: EbookRepository,
    private val ebookImageRepository: EbookImageRepository,
    private val transactionCrudRepository: TransactionCrudRepository,
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
        transactionCrudRepository.deleteAll()
        ebookImageRepository.deleteAll()
        previewImageRepository.deleteAll()
        ebookRepository.deleteAll()
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
    fun `전자책을 구매할 수 있다`(): Unit = runBlocking {
        val (_, createEbookResponse) = postCreateEbook()
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = createEbookResponse.ebook.id.toString(),
            paymentMethod = PaymentMethod.CREDIT_CARD.name,
            price = createEbookResponse.ebook.price
        )

        val tokenGroup = tokenService.createTokenGroup(expectedMember2)
        val accessToken = tokenGroup.accessToken

        val createTransactionResponse = webTestClient
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

        assertThat(createTransactionResponse.ebookId).isEqualTo(createEbookResponse.ebook.id)
        assertThat(createTransactionResponse.price).isEqualTo(createEbookResponse.ebook.price)
        assertThat(createTransactionResponse.paymentMethod).isEqualTo(PaymentMethod.CREDIT_CARD)
    }

    @Test
    fun `전자책을 구매할 때 전자책이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = UUID.randomUUID().toString(),
            paymentMethod = PaymentMethod.CREDIT_CARD.name,
            price = 1000
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
            .expectStatus().isNotFound
    }

    @Test
    fun `전자책을 구매할 때 가격이 다를 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, createEbookResponse) = postCreateEbook()
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = createEbookResponse.ebook.id.toString(),
            paymentMethod = PaymentMethod.CREDIT_CARD.name,
            price = 1000
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
            .expectStatus().isBadRequest
    }

    @Test
    fun `전자책을 구매할 때 자신의 책을 구매할 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, createEbookResponse) = postCreateEbook()
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = createEbookResponse.ebook.id.toString(),
            paymentMethod = PaymentMethod.CREDIT_CARD.name,
            price = createEbookResponse.ebook.price
        )

        val tokenGroup = tokenService.createTokenGroup(expectedMember1)
        val accessToken = tokenGroup.accessToken

        webTestClient
            .post()
            .uri("/api/v1/transactions")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createTransactionRequest)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `전자책을 구매할 때 이미 구매했을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, createEbookResponse) = postCreateEbook()
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = createEbookResponse.ebook.id.toString(),
            paymentMethod = PaymentMethod.CREDIT_CARD.name,
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

        webTestClient
            .post()
            .uri("/api/v1/transactions")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createTransactionRequest)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT.code())
    }

    @Test
    fun `구매한 거래를 조회할 수 있다`(): Unit = runBlocking {
        val (createEbookResponse, accessToken, createTransactionResponse) = postCreateTransaction()

        val getBuyHistoriesResponse = webTestClient
            .get()
            .uri("/api/v1/transactions/buy-histories?page=1&count=10")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetBuyHistoriesResponse>()
            .returnResult()
            .responseBody!!

        val transaction = getBuyHistoriesResponse.transactionList[0]
        assertThat(transaction.id).isEqualTo(createTransactionResponse.transactionId)
        assertThat(transaction.transactionDate.toEpochMilli())
            .isEqualTo(createTransactionResponse.transactionDate.toEpochMilli())
        assertThat(transaction.price).isEqualTo(createEbookResponse.ebook.price)
        assertThat(transaction.ebookId).isEqualTo(createEbookResponse.ebook.id)
    }

    @Test
    fun `구매한 거래를 책 이름으로 검색할 수 있다`(): Unit = runBlocking {
        val (createEbookResponse, accessToken, createTransactionResponse) = postCreateTransaction()

        val getBuyHistoriesResponse = webTestClient
            .get()
            .uri("/api/v1/transactions/buy-histories?page=1&count=10&ebookTitle=${createEbookResponse.ebook.title.first()}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetBuyHistoriesResponse>()
            .returnResult()
            .responseBody!!

        val transaction = getBuyHistoriesResponse.transactionList[0]
        assertThat(transaction.id).isEqualTo(createTransactionResponse.transactionId)
        assertThat(transaction.transactionDate.toEpochMilli())
            .isEqualTo(createTransactionResponse.transactionDate.toEpochMilli())
        assertThat(transaction.price).isEqualTo(createEbookResponse.ebook.price)
        assertThat(transaction.ebookId).isEqualTo(createEbookResponse.ebook.id)
    }

    @Test
    fun `판매한 거래를 조회할 수 있다`(): Unit = runBlocking {
        val (createEbookResponse, _, createTransactionResponse) = postCreateTransaction()
        val tokenGroup = tokenService.createTokenGroup(expectedMember1)
        val accessToken = tokenGroup.accessToken

        val getSellHistoriesResponse = webTestClient
            .get()
            .uri("/api/v1/transactions/sell-histories?page=1&count=10")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetSellHistoriesResponse>()
            .returnResult()
            .responseBody!!

        val transaction = getSellHistoriesResponse.transactionList[0]
        assertThat(transaction.id).isEqualTo(createTransactionResponse.transactionId)
        assertThat(transaction.transactionDate.toEpochMilli())
            .isEqualTo(createTransactionResponse.transactionDate.toEpochMilli())
        assertThat(transaction.price).isEqualTo(createEbookResponse.ebook.price)
        assertThat(transaction.ebookId).isEqualTo(createEbookResponse.ebook.id)
    }

    private suspend fun TransactionControllerTest.postCreateTransaction(): Triple<CreateEbookResponse, AccessToken, CreateTransactionResponse> {
        val (_, createEbookResponse) = postCreateEbook()
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = createEbookResponse.ebook.id.toString(),
            paymentMethod = PaymentMethod.CREDIT_CARD.name,
            price = createEbookResponse.ebook.price
        )

        val tokenGroup = tokenService.createTokenGroup(expectedMember2)
        val accessToken = tokenGroup.accessToken

        val createTransactionResponse = webTestClient
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
        return Triple(createEbookResponse, accessToken, createTransactionResponse)
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
    ): List<EbookImageDto> {
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
