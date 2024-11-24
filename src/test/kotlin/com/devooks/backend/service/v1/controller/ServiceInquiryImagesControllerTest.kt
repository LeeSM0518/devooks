package com.devooks.backend.service.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.common.domain.ImageExtension
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.MemberRepository
import com.devooks.backend.service.v1.dto.request.SaveServiceInquiryImagesRequest
import com.devooks.backend.service.v1.dto.response.SaveServiceInquiryImagesResponse
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
internal class ServiceInquiryImagesControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    private val serviceInquiryImageCrudRepository: ServiceInquiryImageCrudRepository,
) {
    lateinit var expectedMember: Member

    @BeforeEach
    fun setup(): Unit = runBlocking {
        expectedMember = memberRepository.save(MemberEntity(nickname = "nickname")).toDomain()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        serviceInquiryImageCrudRepository.deleteAll()
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
    fun `서비스 문의 사진 목록을 저장할 수 있다`(): Unit = runBlocking {
        val tokenGroup = tokenService.createTokenGroup(expectedMember)
        val accessToken = tokenGroup.accessToken
        val imagePath = Path(javaClass.classLoader.getResource("test.jpg")!!.path)
        val imageBytes = Files.readAllBytes(imagePath)
        val imageBase64Raw = Base64.getEncoder().encodeToString(imageBytes)

        val request = SaveServiceInquiryImagesRequest(
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

        val imageList = webTestClient
            .post()
            .uri("/api/v1/service-inquiries/images")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<SaveServiceInquiryImagesResponse>()
            .returnResult()
            .responseBody!!
            .imageList

        imageList.forEachIndexed { index, image ->
            assertThat(image.order).isEqualTo(index)
            assertThat(File(image.imagePath).exists()).isTrue()
        }
    }
}
