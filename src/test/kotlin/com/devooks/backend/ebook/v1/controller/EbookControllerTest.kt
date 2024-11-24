package com.devooks.backend.ebook.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.ebook.v1.domain.EbookImageType.DESCRIPTION
import com.devooks.backend.ebook.v1.domain.EbookImageType.MAIN
import com.devooks.backend.ebook.v1.dto.EbookImageDto
import com.devooks.backend.ebook.v1.dto.EbookImageDto.Companion.toDto
import com.devooks.backend.ebook.v1.dto.EbookView
import com.devooks.backend.ebook.v1.dto.request.CreateEbookRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookRequest
import com.devooks.backend.ebook.v1.dto.request.SaveDescriptionImagesRequest
import com.devooks.backend.ebook.v1.dto.request.SaveMainImageRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetDetailOfEbookResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookResponse
import com.devooks.backend.ebook.v1.dto.response.SaveDescriptionImagesResponse
import com.devooks.backend.ebook.v1.dto.response.SaveMainImageResponse
import com.devooks.backend.ebook.v1.repository.EbookImageRepository
import com.devooks.backend.ebook.v1.repository.EbookRepository
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.MemberRepository
import com.devooks.backend.notification.v1.adapter.out.persistence.NotificationRepository
import com.devooks.backend.pdf.v1.dto.PdfDto
import com.devooks.backend.pdf.v1.dto.UploadPdfResponse
import com.devooks.backend.pdf.v1.repository.PdfRepository
import com.devooks.backend.pdf.v1.repository.PreviewImageRepository
import com.devooks.backend.review.v1.dto.CreateReviewRequest
import com.devooks.backend.review.v1.dto.CreateReviewResponse
import com.devooks.backend.review.v1.repository.ReviewRepository
import com.devooks.backend.transaciton.v1.domain.PaymentMethod
import com.devooks.backend.transaciton.v1.dto.CreateTransactionRequest
import com.devooks.backend.transaciton.v1.dto.CreateTransactionResponse
import com.devooks.backend.transaciton.v1.repository.TransactionCrudRepository
import com.devooks.backend.wishlist.v1.dto.CreateWishlistRequest
import com.devooks.backend.wishlist.v1.dto.CreateWishlistResponse
import com.devooks.backend.wishlist.v1.repository.WishlistCrudRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.fileSize
import kotlinx.coroutines.flow.first
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
internal class EbookControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val pdfRepository: PdfRepository,
    private val previewImageRepository: PreviewImageRepository,
    private val ebookRepository: EbookRepository,
    private val ebookImageRepository: EbookImageRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionCrudRepository: TransactionCrudRepository,
    private val reviewRepository: ReviewRepository,
    private val wishlistCrudRepository: WishlistCrudRepository,
    private val notificationRepository: NotificationRepository,
) {
    lateinit var expectedMember1: Member
    lateinit var expectedMember2: Member

    @BeforeEach
    fun setup(): Unit = runBlocking {
        expectedMember1 = memberRepository.save(MemberEntity(nickname = "nickname")).toDomain()
        expectedMember2 = memberRepository.save(MemberEntity(nickname = "nickname2")).toDomain()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        wishlistCrudRepository.deleteAll()
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
    fun `PDF 파일을 저장한 후에 전자책을 생성할 수 있다`(): Unit = runBlocking {
        val (request, response) = postCreateEbook()

        val pdfEntity = pdfRepository.findAll().first()
        val ebookEntity = ebookRepository.findAll().first()
        val descriptionImageEntityList = ebookImageRepository.findAll().toList()
        assertThat(response.ebook.pdfId).isEqualTo(pdfEntity.id)
        assertThat(response.ebook.id).isEqualTo(ebookEntity.id)
        assertThat(response.ebook.title).isEqualTo(request.title)
        assertThat(response.ebook.introduction).isEqualTo(request.introduction)
        assertThat(response.ebook.price).isEqualTo(request.price)
        assertThat(response.ebook.tableOfContents).isEqualTo(request.tableOfContents)
        assertThat(response.ebook.relatedCategoryIdList[0].toString()).isEqualTo(request.relatedCategoryIdList!![0])

        val mainImage = response.ebook.mainImage
        assertThat(mainImage.id).isEqualTo(ebookEntity.mainImageId)


        val descriptionImage1 = response.ebook.descriptionImageList[0]
        val foundDescriptionImage1 = descriptionImageEntityList.find { it.id == descriptionImage1.id }
        assertThat(Path(descriptionImage1.imagePath).exists()).isTrue()
        assertThat(descriptionImage1.imagePath).isEqualTo(foundDescriptionImage1!!.imagePath)


        val descriptionImage2 = response.ebook.descriptionImageList[1]
        val foundDescriptionImage2 = descriptionImageEntityList.find { it.id == descriptionImage2.id }
        assertThat(Path(descriptionImage2.imagePath).exists()).isTrue()
        assertThat(descriptionImage2.imagePath).isEqualTo(foundDescriptionImage2!!.imagePath)
    }

    @Test
    fun `전자책을 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()

        val ebookPage = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!

        val pageable = ebookPage.pageable
        val ebookViewList = ebookPage.data
        val ebookView = ebookViewList[0]

        assertThat(pageable.totalPages).isEqualTo(1)
        assertThat(pageable.totalElements).isEqualTo(1)
        assertThat(ebookViewList.size).isEqualTo(1)
        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.price).isEqualTo(response.ebook.price)
        assertThat(ebookView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책이 존재하지 않을 경우 빈 리스트가 조회된다`(): Unit = runBlocking {
        val ebookList = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        assertThat(ebookList.isEmpty()).isTrue()
    }

    @Test
    fun `전자책을 삭제할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk

        assertThat(ebookRepository.findById(response.ebook.id)!!.deletedDate).isNotNull
    }

    @Test
    fun `삭제된 전자책은 조회되지 않는다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk

        val ebookList = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        assertThat(ebookList.isEmpty()).isTrue()
    }

    @Test
    fun `전자책 삭제시 자신이 등록한 전자책이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `전자책을 제목으로 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()

        val ebookViewList = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10&title=${response.ebook.title[0]}")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        val ebookView = ebookViewList[0]
        assertThat(ebookViewList.size).isEqualTo(1)
        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.price).isEqualTo(response.ebook.price)
        assertThat(ebookView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책을 판매자로 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()

        val ebookViewList = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10&sellingMemberId=${response.ebook.sellingMemberId}")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        val ebookView = ebookViewList[0]
        assertThat(ebookViewList.size).isEqualTo(1)
        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.price).isEqualTo(response.ebook.price)
        assertThat(ebookView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책을 식별자로 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()

        val ebookViewList = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10&ebookIdList=${response.ebook.id}")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        val ebookView = ebookViewList[0]
        assertThat(ebookViewList.size).isEqualTo(1)
        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.price).isEqualTo(response.ebook.price)
        assertThat(ebookView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책을 카테고리로 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val categoryId = categoryRepository.findAll().toList()[0].id


        val ebookViewList = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10&categoryIdList=${categoryId}")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        val ebookView = ebookViewList[0]
        assertThat(ebookViewList.size).isEqualTo(1)
        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.price).isEqualTo(response.ebook.price)
        assertThat(ebookView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책을 리뷰순으로 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()

        val ebookViewList = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10&orderBy=REVIEW")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        val ebookView = ebookViewList[0]
        assertThat(ebookViewList.size).isEqualTo(1)
        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.price).isEqualTo(response.ebook.price)
        assertThat(ebookView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책을 제목, 판매자, 식별자, 카테고리로 조회하며 리뷰순으로 정렬할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()

        val ebookViewList = webTestClient
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/ebooks")
                    .queryParam("page", 1)
                    .queryParam("count", 10)
                    .queryParam("title", response.ebook.title[0])
                    .queryParam("sellingMemberId", response.ebook.sellingMemberId)
                    .queryParam("orderBy", "REVIEW")
                    .build()
            }
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data

        val ebookView = ebookViewList[0]
        assertThat(ebookViewList.size).isEqualTo(1)
        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.price).isEqualTo(response.ebook.price)
        assertThat(ebookView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책의 리뷰 평점과 개수를 확인할 수 있다`(): Unit = runBlocking {
        val (response, accessToken) = postCreateEbookAndCreateTransaction()

        val createReviewRequest =
            CreateReviewRequest(
                ebookId = response.ebook.id.toString(),
                rating = "5",
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

        val ebookView = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10&orderBy=REVIEW")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data[0]

        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.wishlistId).isNull()
        assertThat(ebookView.review.rating).isEqualTo(createReviewResponse.review.rating.toDouble())
        assertThat(ebookView.review.count).isOne()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책을 조회하여 찜 식별자를 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        val wishlistId = webTestClient
            .post()
            .uri("/api/v1/wishlist")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(CreateWishlistRequest(response.ebook.id.toString()))
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateWishlistResponse>()
            .returnResult()
            .responseBody!!
            .wishlistId

        val ebookView = webTestClient
            .get()
            .uri("/api/v1/ebooks?page=1&count=10")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<EbookView>>()
            .returnResult()
            .responseBody!!
            .data[0]

        assertThat(ebookView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookView.title).isEqualTo(response.ebook.title)
        assertThat(ebookView.wishlistId).isEqualTo(wishlistId)
        assertThat(ebookView.review.rating).isZero()
        assertThat(ebookView.review.count).isZero()
        assertThat(ebookView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
    }

    @Test
    fun `전자책 상세 조회를 할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        val wishlistId = webTestClient
            .post()
            .uri("/api/v1/wishlist")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(CreateWishlistRequest(response.ebook.id.toString()))
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateWishlistResponse>()
            .returnResult()
            .responseBody!!
            .wishlistId

        val ebookDetailView = webTestClient
            .get()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetDetailOfEbookResponse>()
            .returnResult()
            .responseBody!!
            .ebook

        assertThat(ebookDetailView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookDetailView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookDetailView.title).isEqualTo(response.ebook.title)
        assertThat(ebookDetailView.wishlistId).isEqualTo(wishlistId)
        assertThat(ebookDetailView.review.rating).isZero()
        assertThat(ebookDetailView.review.count).isZero()
        assertThat(ebookDetailView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
        assertThat(ebookDetailView.price).isEqualTo(response.ebook.price)
        assertThat(ebookDetailView.createdDate.toEpochMilli()).isEqualTo(response.ebook.createdDate.toEpochMilli())
        assertThat(ebookDetailView.modifiedDate.toEpochMilli()).isEqualTo(response.ebook.modifiedDate.toEpochMilli())
        assertThat(ebookDetailView.descriptionImageList).isEqualTo(response.ebook.descriptionImageList)
        assertThat(ebookDetailView.introduction).isEqualTo(response.ebook.introduction)
        assertThat(ebookDetailView.tableOfContents).isEqualTo(response.ebook.tableOfContents)
        assertThat(ebookDetailView.pdfId).isEqualTo(response.ebook.pdfId)
        assertThat(ebookDetailView.pageCount).isEqualTo(9)
        assertThat(ebookDetailView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookDetailView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookDetailView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
    }

    @Test
    fun `삭제된 전자책을 상세 조회시 예외가 발생한다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        webTestClient
            .delete()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk

        webTestClient
            .get()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `설명 사진이 존재하지 않는 전자책을 상세 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbookWithNoneDescriptionImageList()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        val wishlistId = webTestClient
            .post()
            .uri("/api/v1/wishlist")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(CreateWishlistRequest(response.ebook.id.toString()))
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateWishlistResponse>()
            .returnResult()
            .responseBody!!
            .wishlistId

        val ebookDetailView = webTestClient
            .get()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetDetailOfEbookResponse>()
            .returnResult()
            .responseBody!!
            .ebook

        assertThat(ebookDetailView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookDetailView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookDetailView.title).isEqualTo(response.ebook.title)
        assertThat(ebookDetailView.wishlistId).isEqualTo(wishlistId)
        assertThat(ebookDetailView.review.rating).isZero()
        assertThat(ebookDetailView.review.count).isZero()
        assertThat(ebookDetailView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
        assertThat(ebookDetailView.price).isEqualTo(response.ebook.price)
        assertThat(ebookDetailView.createdDate.toEpochMilli()).isEqualTo(response.ebook.createdDate.toEpochMilli())
        assertThat(ebookDetailView.modifiedDate.toEpochMilli()).isEqualTo(response.ebook.modifiedDate.toEpochMilli())
        assertThat(ebookDetailView.descriptionImageList).isEmpty()
        assertThat(ebookDetailView.introduction).isEqualTo(response.ebook.introduction)
        assertThat(ebookDetailView.tableOfContents).isEqualTo(response.ebook.tableOfContents)
        assertThat(ebookDetailView.pdfId).isEqualTo(response.ebook.pdfId)
        assertThat(ebookDetailView.pageCount).isEqualTo(9)
        assertThat(ebookDetailView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookDetailView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookDetailView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
    }

    @Test
    fun `전자책 상세 조회하여 리뷰 평점과 개수를 알 수 있다`(): Unit = runBlocking {
        val (response, accessToken) = postCreateEbookAndCreateTransaction()

        val createReviewRequest =
            CreateReviewRequest(
                ebookId = response.ebook.id.toString(),
                rating = "5",
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

        val ebookDetailView = webTestClient
            .get()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetDetailOfEbookResponse>()
            .returnResult()
            .responseBody!!
            .ebook

        assertThat(ebookDetailView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookDetailView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookDetailView.title).isEqualTo(response.ebook.title)
        assertThat(ebookDetailView.wishlistId).isNull()
        assertThat(ebookDetailView.review.rating).isEqualTo(createReviewResponse.review.rating.toDouble())
        assertThat(ebookDetailView.review.count).isOne()
        assertThat(ebookDetailView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
        assertThat(ebookDetailView.price).isEqualTo(response.ebook.price)
        assertThat(ebookDetailView.createdDate.toEpochMilli()).isEqualTo(response.ebook.createdDate.toEpochMilli())
        assertThat(ebookDetailView.modifiedDate.toEpochMilli()).isEqualTo(response.ebook.modifiedDate.toEpochMilli())
        assertThat(ebookDetailView.descriptionImageList).containsAll(response.ebook.descriptionImageList)
        assertThat(ebookDetailView.introduction).isEqualTo(response.ebook.introduction)
        assertThat(ebookDetailView.tableOfContents).isEqualTo(response.ebook.tableOfContents)
        assertThat(ebookDetailView.pdfId).isEqualTo(response.ebook.pdfId)
        assertThat(ebookDetailView.pageCount).isEqualTo(9)
        assertThat(ebookDetailView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookDetailView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookDetailView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
    }

    @Test
    fun `전자책 상세 조회하여 찜 식별자를 조회할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()

        val ebookDetailView = webTestClient
            .get()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetDetailOfEbookResponse>()
            .returnResult()
            .responseBody!!
            .ebook

        assertThat(ebookDetailView.id).isEqualTo(response.ebook.id)
        assertThat(File(ebookDetailView.mainImage.imagePath).exists()).isTrue()
        assertThat(ebookDetailView.title).isEqualTo(response.ebook.title)
        assertThat(ebookDetailView.wishlistId).isNull()
        assertThat(ebookDetailView.review.rating).isZero()
        assertThat(ebookDetailView.review.count).isZero()
        assertThat(ebookDetailView.relatedCategoryIdList).contains(response.ebook.relatedCategoryIdList[0])
        assertThat(ebookDetailView.price).isEqualTo(response.ebook.price)
        assertThat(ebookDetailView.createdDate.toEpochMilli()).isEqualTo(response.ebook.createdDate.toEpochMilli())
        assertThat(ebookDetailView.modifiedDate.toEpochMilli()).isEqualTo(response.ebook.modifiedDate.toEpochMilli())
        assertThat(ebookDetailView.descriptionImageList).isEqualTo(response.ebook.descriptionImageList)
        assertThat(ebookDetailView.introduction).isEqualTo(response.ebook.introduction)
        assertThat(ebookDetailView.tableOfContents).isEqualTo(response.ebook.tableOfContents)
        assertThat(ebookDetailView.pdfId).isEqualTo(response.ebook.pdfId)
        assertThat(ebookDetailView.pageCount).isEqualTo(9)
        assertThat(ebookDetailView.seller.id).isEqualTo(expectedMember1.id)
        assertThat(ebookDetailView.seller.nickname).isEqualTo(expectedMember1.nickname)
        assertThat(ebookDetailView.seller.profileImagePath).isEqualTo(expectedMember1.profileImagePath)
    }

    @Test
    fun `전자책을 수정할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val imagePath = Path(javaClass.classLoader.getResource("test.jpg")!!.path)
        val imageBytes = Files.readAllBytes(imagePath)
        val imageBase64Raw = Base64.getEncoder().encodeToString(imageBytes)

        val newMainImage = postSaveMainImage(imageBase64Raw, imagePath, accessToken)
        val newDescriptionImages = postSaveDescriptionImages(imageBase64Raw, imagePath, accessToken)
        val categoryId = categoryRepository.findAll().toList()[1].id!!.toString()

        val modifyEbookRequest = ModifyEbookRequest(
            ebook = ModifyEbookRequest.Ebook(
                title = "title2",
                relatedCategoryIdList = listOf(categoryId),
                mainImageId = newMainImage.id.toString(),
                descriptionImageIdList =
                newDescriptionImages
                    .map { it.id.toString() }
                    .plus(response.ebook.descriptionImageList.map { it.id.toString() }.first()),
                price = 20000,
                tableOfContents = "tableOfContents2",
                introduction = "introduction2"
            ),
        )

        val updatedEbook = webTestClient
            .patch()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(modifyEbookRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyEbookResponse>()
            .returnResult()
            .responseBody!!
            .ebook

        assertThat(updatedEbook.id).isEqualTo(response.ebook.id)
        assertThat(updatedEbook.mainImage.id).isEqualTo(newMainImage.id)
        assertThat(updatedEbook.mainImage.imagePath).isEqualTo(newMainImage.imagePath)
        assertThat(updatedEbook.title).isEqualTo(modifyEbookRequest.ebook!!.title)
        assertThat(updatedEbook.relatedCategoryIdList.map { it.toString() })
            .containsAll(modifyEbookRequest.ebook!!.relatedCategoryIdList!!)
        assertThat(updatedEbook.price).isEqualTo(modifyEbookRequest.ebook!!.price)
        val expected = modifyEbookRequest.ebook!!.descriptionImageIdList!!.map { UUID.fromString(it) }
        assertThat(updatedEbook.descriptionImageList.map { it.id }).isEqualTo(expected)
        assertThat(updatedEbook.introduction).isEqualTo(modifyEbookRequest.ebook!!.introduction)
        assertThat(updatedEbook.tableOfContents).isEqualTo(modifyEbookRequest.ebook!!.tableOfContents)
    }

    @Test
    fun `삭제된 전자책을 수정시 예외가 발생한다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken
        val imagePath = Path(javaClass.classLoader.getResource("test.jpg")!!.path)
        val imageBytes = Files.readAllBytes(imagePath)
        val imageBase64Raw = Base64.getEncoder().encodeToString(imageBytes)

        val newMainImage = postSaveMainImage(imageBase64Raw, imagePath, accessToken)
        val newDescriptionImages = postSaveDescriptionImages(imageBase64Raw, imagePath, accessToken)

        val categoryId = categoryRepository.findAll().toList()[1].id.toString()

        webTestClient
            .delete()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk

        val modifyEbookRequest = ModifyEbookRequest(
            ebook = ModifyEbookRequest.Ebook(
                title = "title2",
                relatedCategoryIdList = listOf(categoryId),
                mainImageId = newMainImage.id.toString(),
                descriptionImageIdList =
                newDescriptionImages
                    .map { it.id.toString() }
                    .plus(response.ebook.descriptionImageList.map { it.id.toString() }.first()),
                price = 20000,
                tableOfContents = "tableOfContents2",
                introduction = "introduction2"
            )
        )

        webTestClient
            .patch()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(modifyEbookRequest)
            .exchange()
            .expectStatus().isNotFound

    }

    @Test
    fun `전자책의 제목만 수정할 수 있다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val originEbookEntity = ebookRepository.findById(response.ebook.id)!!
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        val modifyEbookRequest = ModifyEbookRequest(
            ebook = ModifyEbookRequest.Ebook(
                title = "title2",
            )
        )

        val updatedEbook = webTestClient
            .patch()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(modifyEbookRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyEbookResponse>()
            .returnResult()
            .responseBody!!
            .ebook

        val mainImage =
            ebookImageRepository
                .findAllByEbookIdAndImageType(updatedEbook.id, MAIN)
                .first().toDomain().toDto()

        val descriptionImageList =
            ebookImageRepository
                .findAllByEbookIdAndImageType(updatedEbook.id, DESCRIPTION)
                .map { it.toDomain().toDto() }

        assertThat(updatedEbook.id).isEqualTo(response.ebook.id)
        assertThat(updatedEbook.mainImage).isEqualTo(mainImage)
        assertThat(updatedEbook.title).isEqualTo(modifyEbookRequest.ebook!!.title)
        assertThat(updatedEbook.descriptionImageList).isEqualTo(descriptionImageList)
    }

    @Test
    fun `전자책의 제목 수정시 제목이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        val modifyEbookRequest = ModifyEbookRequest(
            ebook = ModifyEbookRequest.Ebook(title = "")
        )

        webTestClient
            .patch()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(modifyEbookRequest)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `전자책 수정시 전자책이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val accessToken = tokenService.createTokenGroup(expectedMember1).accessToken

        val modifyEbookRequest = ModifyEbookRequest(
            ebook = ModifyEbookRequest.Ebook(
                title = "title2",
            )
        )

        webTestClient
            .patch()
            .uri("/api/v1/ebooks/${UUID.randomUUID()}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(modifyEbookRequest)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `전자책 수정시 자신이 판매하는 전자책이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, response) = postCreateEbook()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        val modifyEbookRequest = ModifyEbookRequest(
            ebook = ModifyEbookRequest.Ebook(
                title = "title2",
            )
        )

        webTestClient
            .patch()
            .uri("/api/v1/ebooks/${response.ebook.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(modifyEbookRequest)
            .exchange()
            .expectStatus().isForbidden
    }

    suspend fun postCreateEbookAndCreateTransaction(): Pair<CreateEbookResponse, AccessToken> {
        val (_, response) = postCreateEbook()
        val createTransactionRequest = CreateTransactionRequest(
            ebookId = response.ebook.id.toString(),
            paymentMethod = PaymentMethod.CREDIT_CARD.name,
            price = response.ebook.price
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
        return Pair(response, accessToken)
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
                    imagePath.fileSize()
                ),
                ImageDto(
                    imageBase64Raw,
                    imagePath.extension,
                    imagePath.fileSize()
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
    ): EbookImageDto {
        val saveMainImageRequest = SaveMainImageRequest(
            ImageDto(
                base64Raw = imageBase64Raw,
                extension = imagePath.extension,
                byteSize = imagePath.fileSize()
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
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()

        val createEbookRequest = CreateEbookRequest(
            pdfId = pdf.id.toString(),
            title = "title",
            relatedCategoryIdList = listOf(categoryId),
            mainImageId = mainImage.id.toString(),
            descriptionImageIdList = listOf(),
            10000,
            "introduction",
            "tableOfContent"
        )
        val request = createEbookRequest
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
