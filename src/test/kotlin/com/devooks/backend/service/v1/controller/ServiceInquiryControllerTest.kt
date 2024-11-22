package com.devooks.backend.service.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.MemberRepository
import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto
import com.devooks.backend.service.v1.dto.request.CreateServiceInquiryRequest
import com.devooks.backend.service.v1.dto.request.ModifyServiceInquiryRequest
import com.devooks.backend.service.v1.dto.request.SaveServiceInquiryImagesRequest
import com.devooks.backend.service.v1.dto.response.CreateServiceInquiryResponse
import com.devooks.backend.service.v1.dto.response.GetServiceInquiriesResponse
import com.devooks.backend.service.v1.dto.response.ModifyServiceInquiryResponse
import com.devooks.backend.service.v1.dto.response.SaveServiceInquiryImagesResponse
import com.devooks.backend.service.v1.repository.ServiceInquiryCrudRepository
import com.devooks.backend.service.v1.repository.ServiceInquiryImageCrudRepository
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.fileSize
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
internal class ServiceInquiryControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val serviceInquiryImageCrudRepository: ServiceInquiryImageCrudRepository,
    private val serviceInquiryCrudRepository: ServiceInquiryCrudRepository,
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
        serviceInquiryImageCrudRepository.deleteAll()
        serviceInquiryCrudRepository.deleteAll()
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
    fun `서비스 문의를 생성할 수 있다`(): Unit = runBlocking {
        val (accessToken, imageList) = postSaveServiceInquiryImages()

        val createServiceInquiryRequest = CreateServiceInquiryRequest(
            title = "title",
            content = "content",
            imageIdList = imageList.map { it.id.toString() }
        )

        val serviceInquiry = webTestClient
            .post()
            .uri("/api/v1/service-inquiries")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createServiceInquiryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateServiceInquiryResponse>()
            .returnResult()
            .responseBody!!
            .serviceInquiry

        assertThat(serviceInquiry.title).isEqualTo(createServiceInquiryRequest.title)
        assertThat(serviceInquiry.content).isEqualTo(createServiceInquiryRequest.content)
        assertThat(serviceInquiry.writerMemberId).isEqualTo(expectedMember1.id)
        assertThat(serviceInquiry.imageList.map { it.id }).containsAll(imageList.map { it.id })
        assertThat(serviceInquiry.inquiryProcessingStatus).isEqualTo(InquiryProcessingStatus.WAITING)
    }

    @Test
    fun `서비스 문의 생성시 사진이 자신이 업로드한 경우가 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, imageList) = postSaveServiceInquiryImages()
        val accessToken = tokenService.createTokenGroup(expectedMember2).accessToken

        val createServiceInquiryRequest = CreateServiceInquiryRequest(
            title = "title",
            content = "content",
            imageIdList = imageList.map { it.id.toString() }
        )

        webTestClient
            .post()
            .uri("/api/v1/service-inquiries")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createServiceInquiryRequest)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `서비스 문의를 조회할 수 있다`(): Unit = runBlocking {
        val (accessToken, imageList) = postSaveServiceInquiryImages()

        val createServiceInquiryRequest = CreateServiceInquiryRequest(
            title = "title",
            content = "content",
            imageIdList = imageList.map { it.id.toString() }
        )

        val serviceInquiry = webTestClient
            .post()
            .uri("/api/v1/service-inquiries")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createServiceInquiryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateServiceInquiryResponse>()
            .returnResult()
            .responseBody!!
            .serviceInquiry

        val serviceInquiryDto = webTestClient
            .get()
            .uri("/api/v1/service-inquiries?page=1&count=10")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetServiceInquiriesResponse>()
            .returnResult()
            .responseBody!!
            .serviceInquiryList[0]

        assertThat(serviceInquiryDto.id).isEqualTo(serviceInquiry.id)
        assertThat(serviceInquiryDto.title).isEqualTo(serviceInquiry.title)
        assertThat(serviceInquiryDto.imageList).containsAll(serviceInquiry.imageList)
        assertThat(serviceInquiryDto.content).isEqualTo(serviceInquiry.content)
        assertThat(serviceInquiryDto.inquiryProcessingStatus).isEqualTo(serviceInquiry.inquiryProcessingStatus)
        assertThat(serviceInquiryDto.createdDate.toEpochMilli()).isEqualTo(serviceInquiry.createdDate.toEpochMilli())
        assertThat(serviceInquiryDto.modifiedDate.toEpochMilli()).isEqualTo(serviceInquiry.modifiedDate.toEpochMilli())
        assertThat(serviceInquiryDto.writerMemberId).isEqualTo(serviceInquiry.writerMemberId)
    }

    @Test
    fun `서비스 문의를 수정할 수 있다`(): Unit = runBlocking {
        val (accessToken, imageList1) = postSaveServiceInquiryImages()

        val createServiceInquiryRequest = CreateServiceInquiryRequest(
            title = "title",
            content = "content",
            imageIdList = imageList1.map { it.id.toString() }
        )

        val createdServiceInquiry = webTestClient
            .post()
            .uri("/api/v1/service-inquiries")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createServiceInquiryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateServiceInquiryResponse>()
            .returnResult()
            .responseBody!!
            .serviceInquiry

        val (_, imageList2) = postSaveServiceInquiryImages()
        val modifyServiceInquiryRequest = ModifyServiceInquiryRequest(
            serviceInquiry = ModifyServiceInquiryRequest.ServiceInquiry(
                title = "title2",
                content = "content2",
                imageIdList = listOf(imageList1.map { it.id.toString() }.first(), imageList2.first().id.toString())
            )
        )

        val updatedServiceInquiry = webTestClient
            .patch()
            .uri("/api/v1/service-inquiries/${createdServiceInquiry.id}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(modifyServiceInquiryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyServiceInquiryResponse>()
            .returnResult()
            .responseBody!!
            .serviceInquiry

        assertThat(updatedServiceInquiry.id).isEqualTo(createdServiceInquiry.id)
        assertThat(updatedServiceInquiry.title).isEqualTo(modifyServiceInquiryRequest.serviceInquiry!!.title)
        assertThat(updatedServiceInquiry.content).isEqualTo(modifyServiceInquiryRequest.serviceInquiry!!.content)
        assertThat(updatedServiceInquiry.imageList.map { it.id.toString() }).containsAll(modifyServiceInquiryRequest.serviceInquiry!!.imageIdList)
        assertThat(updatedServiceInquiry.writerMemberId).isEqualTo(createdServiceInquiry.writerMemberId)
        assertThat(updatedServiceInquiry.inquiryProcessingStatus).isEqualTo(createdServiceInquiry.inquiryProcessingStatus)
    }

    @Test
    fun `서비스 문의 수정시 자신이 작성한 문의가 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val (accessToken1, imageList1) = postSaveServiceInquiryImages()
        val accessToken2 = tokenService.createTokenGroup(expectedMember2).accessToken

        val createServiceInquiryRequest = CreateServiceInquiryRequest(
            title = "title",
            content = "content",
            imageIdList = imageList1.map { it.id.toString() }
        )

        val createdServiceInquiry = webTestClient
            .post()
            .uri("/api/v1/service-inquiries")
            .header(AUTHORIZATION, "Bearer $accessToken1")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(createServiceInquiryRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<CreateServiceInquiryResponse>()
            .returnResult()
            .responseBody!!
            .serviceInquiry

        val (_, imageList2) = postSaveServiceInquiryImages()
        val modifyServiceInquiryRequest = ModifyServiceInquiryRequest(
            serviceInquiry = ModifyServiceInquiryRequest.ServiceInquiry(
                title = "title2",
                content = "content2",
                imageIdList = listOf(imageList1.map { it.id.toString() }.first(), imageList2.first().id.toString())
            )
        )

        webTestClient
            .patch()
            .uri("/api/v1/service-inquiries/${createdServiceInquiry.id}")
            .header(AUTHORIZATION, "Bearer $accessToken2")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(modifyServiceInquiryRequest)
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `서비스 문의 수정시 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (accessToken, _) = postSaveServiceInquiryImages()

        val modifyServiceInquiryRequest = ModifyServiceInquiryRequest(
            serviceInquiry = ModifyServiceInquiryRequest.ServiceInquiry(
                title = "title2",
                content = "content2",
            )
        )

        webTestClient
            .patch()
            .uri("/api/v1/service-inquiries/${UUID.randomUUID()}")
            .header(AUTHORIZATION, "Bearer $accessToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(modifyServiceInquiryRequest)
            .exchange()
            .expectStatus().isNotFound
    }

    private suspend fun postSaveServiceInquiryImages(): Pair<AccessToken, List<ServiceInquiryImageDto>> {
        val tokenGroup = tokenService.createTokenGroup(expectedMember1)
        val accessToken = tokenGroup.accessToken
        val imagePath = Path(javaClass.classLoader.getResource("test.jpg")!!.path)
        val imageBytes = Files.readAllBytes(imagePath)
        val imageBase64Raw = Base64.getEncoder().encodeToString(imageBytes)

        val serviceInquiryImagesRequest = SaveServiceInquiryImagesRequest(
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

        val imageList = webTestClient
            .post()
            .uri("/api/v1/service-inquiries/images")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(serviceInquiryImagesRequest)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<SaveServiceInquiryImagesResponse>()
            .returnResult()
            .responseBody!!
            .imageList
        return Pair(accessToken, imageList)
    }
}
