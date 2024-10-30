package com.devooks.backend.wishlist.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.ebook.v1.dto.DescriptionImageDto
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
import com.devooks.backend.wishlist.v1.dto.CreateWishlistRequest
import com.devooks.backend.wishlist.v1.dto.CreateWishlistResponse
import com.devooks.backend.wishlist.v1.dto.GetWishlistResponse
import com.devooks.backend.wishlist.v1.repository.WishlistRepository
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
internal class WishlistControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
    private val ebookImageRepository: EbookImageRepository,
    private val ebookRepository: EbookRepository,
    private val wishlistRepository: WishlistRepository,
    private val categoryRepository: CategoryRepository,
) {
    lateinit var expectedMember: Member

    @BeforeEach
    fun setup(): Unit = runBlocking {
        expectedMember = memberRepository.save(MemberEntity(nickname = "nickname")).toDomain()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        wishlistRepository.deleteAll()
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
    fun `전자책을 찜 할 수 있다`(): Unit = runBlocking {
        val (accessToken, createEbookResponse) = postCreateEbook()

        val response = postCreateWishlist(createEbookResponse, accessToken)

        assertThat(response.ebookId).isEqualTo(createEbookResponse.ebook.id)
        assertThat(response.memberId).isEqualTo(expectedMember.id)
    }

    @Test
    fun `존재하지 않는 전자책을 찜할 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember).accessToken
        val request = CreateWishlistRequest(
            ebookId = UUID.randomUUID().toString()
        )

        webTestClient
            .post()
            .uri("/api/v1/wishlist")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `찜이 이미 존재할 경우 예외가 발생한다`(): Unit = runBlocking {
        val (accessToken, createEbookResponse) = postCreateEbook()

        val request = CreateWishlistRequest(
            ebookId = createEbookResponse.ebook.id.toString()
        )

        webTestClient
            .post()
            .uri("/api/v1/wishlist")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk

        webTestClient
            .post()
            .uri("/api/v1/wishlist")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(CONFLICT.code())
    }

    @Test
    fun `찜 목록을 조회할 수 있다`(): Unit = runBlocking {
        val (accessToken, createEbookResponse) = postCreateEbook()

        val response = postCreateWishlist(createEbookResponse, accessToken)

        val wishlist = webTestClient
            .get()
            .uri("/api/v1/wishlist?page=1&count=10")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetWishlistResponse>()
            .returnResult()
            .responseBody!!
            .wishlist

        assertThat(wishlist[0].id).isEqualTo(response.wishlistId)
        assertThat(wishlist[0].ebookId).isEqualTo(response.ebookId)
        assertThat(wishlist[0].memberId).isEqualTo(response.memberId)
    }

    @Test
    fun `찜 목록을 카테고리로 조회할 수 있다`(): Unit = runBlocking {
        val (accessToken, createEbookResponse) = postCreateEbook()

        val response = postCreateWishlist(createEbookResponse, accessToken)

        val wishlist = webTestClient
            .get()
            .uri(
                "/api/v1/wishlist?page=1&count=10&" +
                        "categoryIds=${createEbookResponse.ebook.relatedCategoryList[0].id}&" +
                        "categoryIds=${UUID.randomUUID()}"
            )
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetWishlistResponse>()
            .returnResult()
            .responseBody!!
            .wishlist

        assertThat(wishlist[0].id).isEqualTo(response.wishlistId)
        assertThat(wishlist[0].ebookId).isEqualTo(response.ebookId)
        assertThat(wishlist[0].memberId).isEqualTo(response.memberId)
    }

    @Test
    fun `찜을 삭제할 수 있다`(): Unit = runBlocking {
        val (accessToken, createEbookResponse) = postCreateEbook()

        val response = postCreateWishlist(createEbookResponse, accessToken)

        webTestClient
            .delete()
            .uri("/api/v1/wishlist/${response.wishlistId}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk

        assertThat(wishlistRepository.count()).isZero()
    }

    @Test
    fun `찜 삭제시 자신의 찜이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val (accessToken, createEbookResponse) = postCreateEbook()

        val response = postCreateWishlist(createEbookResponse, accessToken)

        webTestClient
            .delete()
            .uri("/api/v1/wishlist/${response.wishlistId}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer ${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `찜 삭제시 찜이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (accessToken, _) = postCreateEbook()

        webTestClient
            .delete()
            .uri("/api/v1/wishlist/${UUID.randomUUID()}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `찜 삭제시 잘못된 찜 식별자일 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/wishlist/ ")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isBadRequest
    }

    private fun postCreateWishlist(
        createEbookResponse: CreateEbookResponse,
        accessToken: AccessToken,
    ): CreateWishlistResponse {
        val request = CreateWishlistRequest(
            ebookId = createEbookResponse.ebook.id.toString()
        )

        val response = webTestClient
            .post()
            .uri("/api/v1/wishlist")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateWishlistResponse>()
            .returnResult()
            .responseBody!!
        return response
    }

    suspend fun postCreateEbook(): Pair<String, CreateEbookResponse> {
        val tokenGroup = tokenService.createTokenGroup(expectedMember)
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
        return Pair(accessToken, response)
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
