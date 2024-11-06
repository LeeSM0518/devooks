package com.devooks.backend.member.v1.controller

import com.devooks.backend.BackendApplication.Companion.STATIC_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.createDirectories
import com.devooks.backend.auth.v1.domain.Authority
import com.devooks.backend.auth.v1.domain.OauthType
import com.devooks.backend.auth.v1.domain.TokenGroup
import com.devooks.backend.auth.v1.error.AuthError
import com.devooks.backend.auth.v1.repository.OauthInfoRepository
import com.devooks.backend.auth.v1.repository.RefreshTokenRepository
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.domain.Category.Companion.toDomain
import com.devooks.backend.category.v1.dto.CategoryDto.Companion.toDto
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.error.CommonError
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.fixture.ErrorResponse
import com.devooks.backend.fixture.ErrorResponse.Companion.patchForBadRequest
import com.devooks.backend.fixture.ErrorResponse.Companion.patchForConflict
import com.devooks.backend.fixture.ErrorResponse.Companion.postForBadRequest
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.dto.GetProfileResponse
import com.devooks.backend.member.v1.dto.ModifyAccountInfoRequest
import com.devooks.backend.member.v1.dto.ModifyAccountInfoResponse
import com.devooks.backend.member.v1.dto.ModifyNicknameRequest
import com.devooks.backend.member.v1.dto.ModifyNicknameResponse
import com.devooks.backend.member.v1.dto.ModifyProfileImageRequest
import com.devooks.backend.member.v1.dto.ModifyProfileImageResponse
import com.devooks.backend.member.v1.dto.ModifyProfileRequest
import com.devooks.backend.member.v1.dto.ModifyProfileResponse
import com.devooks.backend.member.v1.dto.SignUpRequest
import com.devooks.backend.member.v1.dto.SignUpResponse
import com.devooks.backend.member.v1.dto.WithdrawMemberRequest
import com.devooks.backend.member.v1.error.MemberError
import com.devooks.backend.member.v1.repository.FavoriteCategoryRepository
import com.devooks.backend.member.v1.repository.MemberInfoRepository
import com.devooks.backend.member.v1.repository.MemberRepository
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.util.*
import kotlin.io.path.Path
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatList
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
internal class MemberControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val memberRepository: MemberRepository,
    private val memberInfoRepository: MemberInfoRepository,
    private val oauthInfoRepository: OauthInfoRepository,
    private val categoryRepository: CategoryRepository,
    private val favoriteCategoryRepository: FavoriteCategoryRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenService: TokenService,
) {

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

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        refreshTokenRepository.deleteAll()
        favoriteCategoryRepository.deleteAll()
        memberRepository.deleteAll()
        oauthInfoRepository.deleteAll()
        memberInfoRepository.deleteAll()
    }

    @Test
    fun `회원가입 할 수 있다`(): Unit = runBlocking {
        // given
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER.name,
            nickname = "nickname",
            favoriteCategoryIdList = listOf(categoryId)
        )

        // when
        val response = signUp(request)

        // then
        assertThat(response.member.nickname).isEqualTo(request.nickname)
        assertThat(response.member.authority).isEqualTo(Authority.USER)
        assertThat(response.member.profileImagePath).isEqualTo("")

        val category = categoryRepository.findAll().firstOrNull()!!
        assertThat(category.id.toString()).isEqualTo(request.favoriteCategoryIdList!!.first())

        val favoriteCategory = favoriteCategoryRepository.findAll().firstOrNull()!!
        assertThat(favoriteCategory.categoryId).isEqualTo(category.id)
        assertThat(favoriteCategory.favoriteMemberId).isEqualTo(response.member.id)

        val refreshToken = refreshTokenRepository.findAll().firstOrNull()!!
        assertThat(refreshToken.memberId).isEqualTo(response.member.id)
        assertThat(refreshToken.token).isEqualTo(response.tokenGroup.refreshToken)
    }

    @Test
    fun `oauthId가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthType" : "NAVER",
                  "nickname" : "nickname",
                  "favoriteCategories" : [ "category" ]
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(AuthError.REQUIRED_OAUTH_ID.exception)
    }

    @Test
    fun `oauthId가 빈문자열인 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthId": "",
                  "oauthType" : "NAVER",
                  "nickname" : "nickname",
                  "favoriteCategories" : [ "category" ]
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(AuthError.REQUIRED_OAUTH_ID.exception)
    }

    @Test
    fun `oauthType이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthId" : "oauthId",
                  "nickname" : "nickname",
                  "favoriteCategories" : [ "category" ]
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(AuthError.INVALID_OAUTH_TYPE.exception)
    }

    @Test
    fun `oauthType이 잘못된 형식일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthId" : "oauthId",
                  "oauthType" : " ",
                  "nickname" : "nickname",
                  "favoriteCategories" : [ "category" ]
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(AuthError.INVALID_OAUTH_TYPE.exception)
    }

    @Test
    fun `nickname 이 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthId" : "oauthId",
                  "oauthType" : "NAVER",
                  "favoriteCategories" : [ "category" ]
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(MemberError.REQUIRED_NICKNAME.exception)
    }

    @Test
    fun `nickname 이 2자 미만일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthId" : "oauthId",
                  "oauthType" : "NAVER",
                  "nickname" : "n",
                  "favoriteCategories" : [ "category" ]
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(MemberError.INVALID_NICKNAME.exception)
    }

    @Test
    fun `nickname 이 12자 초과일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthId" : "oauthId",
                  "oauthType" : "NAVER",
                  "nickname" : "1111111111111",
                  "favoriteCategories" : [ "category" ]
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(MemberError.INVALID_NICKNAME.exception)
    }

    @Test
    fun `favoriteCategories 가 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "oauthId" : "oauthId",
                  "oauthType" : "NAVER",
                  "nickname" : "nickname"
                }
            """.trimIndent()
        val response = webTestClient.postForBadRequest("/api/v1/members/signup", request)

        response.isEqualTo(MemberError.REQUIRED_FAVORITE_CATEGORIES.exception)
    }

    @Test
    fun `닉네임이 이미 존재할 경우 회원가입 실패`(): Unit = runBlocking {
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = "NAVER",
            nickname = "nickname",
            favoriteCategoryIdList = listOf(categoryId)
        )
        signUp(request)

        val response = webTestClient
            .post()
            .uri("/api/v1/members/signup")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request.copy(oauthId = "oauthId2"))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody<ErrorResponse>()
            .returnResult()
            .responseBody!!

        response.isEqualTo(MemberError.DUPLICATE_NICKNAME.exception)
    }

    @Test
    fun `정지당한 회원일 경우 회원가입 실패`(): Unit = runBlocking {
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = "NAVER",
            nickname = "nickname",
            favoriteCategoryIdList = listOf(categoryId)
        )
        val signUpResponse = signUp(request)
        val foundMember = memberRepository.findById(signUpResponse.member.id)!!
        memberRepository.save(foundMember.copy(untilSuspensionDate = Instant.MAX))

        val response = webTestClient
            .post()
            .uri("/api/v1/members/signup")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request.copy(nickname = "nickname2"))
            .exchange()
            .expectStatus().isForbidden
            .expectBody<ErrorResponse>()
            .returnResult()
            .responseBody!!

        response.isEqualTo(MemberError.SUSPENDED_MEMBER.exception)
    }

    @Test
    fun `탈퇴한 회원일 경우 회원가입 실패`(): Unit = runBlocking {
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = "NAVER",
            nickname = "nickname",
            favoriteCategoryIdList = listOf(categoryId)
        )
        val signUpResponse = signUp(request)
        val foundMember = memberRepository.findById(signUpResponse.member.id)!!
        memberRepository.save(foundMember.copy(withdrawalDate = Instant.now()))

        val response = webTestClient
            .post()
            .uri("/api/v1/members/signup")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request.copy(nickname = "nickname2"))
            .exchange()
            .expectStatus().isForbidden
            .expectBody<ErrorResponse>()
            .returnResult()
            .responseBody!!

        response.isEqualTo(MemberError.WITHDREW_MEMBER.exception)
    }

    @Test
    fun `이미 존재하는 회원일 경우 회원가입 실패`(): Unit = runBlocking {
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = "NAVER",
            nickname = "nickname",
            favoriteCategoryIdList = listOf(categoryId)
        )
        signUp(request)

        val response = webTestClient
            .post()
            .uri("/api/v1/members/signup")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(request.copy(nickname = "nickname2"))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody<ErrorResponse>()
            .returnResult()
            .responseBody!!

        response.isEqualTo(AuthError.DUPLICATE_OAUTH_ID.exception)
    }

    @Test
    fun `계좌정보를 수정할 수 있다`(): Unit = runBlocking {
        val (signUpResponse, tokenGroup) = signUp()
        val modifyAccountInfoRequest = ModifyAccountInfoRequest(
            realName = "이상민",
            bank = "농협",
            accountNumber = "12312341234",
        )

        val response = webTestClient
            .patch()
            .uri("/api/v1/members/account")
            .header(AUTHORIZATION, "Bearer ${tokenGroup.accessToken}")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(modifyAccountInfoRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyAccountInfoResponse>()
            .returnResult()
            .responseBody!!

        val memberInfo = memberInfoRepository.findByMemberId(signUpResponse.member.id)!!

        assertThat(response.bank).isEqualTo(memberInfo.bank)
        assertThat(response.accountNumber).isEqualTo(memberInfo.accountNumber)
        assertThat(response.realName).isEqualTo(memberInfo.realName)
    }

    @Test
    fun `프로필 사진을 수정할 수 있다`(): Unit = runBlocking {
        val (signUpResponse, tokenGroup) = signUp()
        val modifyProfileImageRequest = ModifyProfileImageRequest(
            image = ImageDto(
                "test",
                "png",
                4,
                1
            )
        )

        val response = webTestClient
            .patch()
            .uri("/api/v1/members/image")
            .header(AUTHORIZATION, "Bearer ${tokenGroup.accessToken}")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(modifyProfileImageRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyProfileImageResponse>()
            .returnResult()
            .responseBody!!

        val member = response.member
        assertThat(member.id).isEqualTo(signUpResponse.member.id)
        assertThat(member.nickname).isEqualTo(signUpResponse.member.nickname)
        assertThat(File(member.profileImagePath).canRead()).isEqualTo(true)

        Files.delete(Path(member.profileImagePath))
    }

    @Test
    fun `닉네임을 수정할 수 있다`(): Unit = runBlocking {
        val (signUpResponse, tokenGroup) = signUp()
        val modifyNicknameRequest = ModifyNicknameRequest(
            nickname = "test"
        )

        val response = webTestClient
            .patch()
            .uri("/api/v1/members/nickname")
            .header(AUTHORIZATION, "Bearer ${tokenGroup.accessToken}")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(modifyNicknameRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyNicknameResponse>()
            .returnResult()
            .responseBody!!

        val member = response.member
        assertThat(member.id).isEqualTo(signUpResponse.member.id)
        assertThat(member.nickname).isEqualTo(modifyNicknameRequest.nickname)
    }

    @Test
    fun `프로필을 수정할 수 있다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()
        val modifyProfileRequest =
            ModifyProfileRequest(
                phoneNumber = "010-1234-1234",
                blogLink = "www.naver.com",
                instagramLink = "www.instagram.com",
                youtubeLink = "www.youtube.com",
                introduction = "hello",
                favoriteCategoryIdList = listOf(categoryId),
                email = "asd@naver.com"
            )

        postModifyProfile(tokenGroup, modifyProfileRequest)
    }

    @Test
    fun `프로필을 조회할 수 있다`(): Unit = runBlocking {
        val (signUpResponse, _) = signUp()

        val response = webTestClient
            .get()
            .uri("/api/v1/members/${signUpResponse.member.id}/profile")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<GetProfileResponse>()
            .returnResult()
            .responseBody!!

        val categoryDto = categoryRepository.findAll().toList()[0].toDomain().toDto().id

        assertThat(response.memberId).isEqualTo(signUpResponse.member.id)
        assertThat(response.nickname).isEqualTo(signUpResponse.member.nickname)
        assertThat(response.profileImagePath).isEqualTo(signUpResponse.member.profileImagePath)
        assertThat(response.favoriteCategoryIdList.firstOrNull()).isEqualTo(categoryDto)
        assertThat(response.profile.blogLink).isEqualTo("")
        assertThat(response.profile.youtubeLink).isEqualTo("")
        assertThat(response.profile.introduction).isEqualTo("")
        assertThat(response.profile.instagramLink).isEqualTo("")
    }

    @Test
    fun `프로필 조회시 회원이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        webTestClient
            .get()
            .uri("/api/v1/members/${UUID.randomUUID()}/profile")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `회원 탈퇴할 수 있다`(): Unit = runBlocking {
        val (response, tokenGroup) = signUp()

        webTestClient
            .patch()
            .uri("/api/v1/members/withdrawal")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(WithdrawMemberRequest(withdrawalReason = "reason"))
            .header(AUTHORIZATION, "Bearer ${tokenGroup.accessToken}")
            .exchange()
            .expectStatus().isOk

        val member = memberRepository.findById(response.member.id)
        assertThat(member!!.withdrawalDate).isNotNull()
    }

    @Test
    fun `닉네임 수정시 nickname 이 비어 있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "nickname" : ""
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/nickname", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.REQUIRED_NICKNAME.exception)
    }

    @Test
    fun `닉네임 수정시 nickname 이 이미 존재할 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = """
                {
                  "nickname" : "nickname"
                }
            """.trimIndent()
        val response =
            webTestClient
                .patchForConflict("/api/v1/members/nickname", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.DUPLICATE_NICKNAME.exception)
    }

    @Test
    fun `image 가 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)

        response.isEqualTo(CommonError.REQUIRED_IMAGE.exception)
    }

    @Test
    fun `base64Raw 가 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "image" : {
                    "base64Raw" : "",
                    "extension" : "PNG",
                    "byteSize" : 4
                  }
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)

        response.isEqualTo(CommonError.REQUIRED_BASE64RAW.exception)
    }

    @Test
    fun `extension 이 JPG, PNG, JPEG 이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "image" : {
                    "base64Raw" : "test",
                    "extension" : "JJP",
                    "byteSize" : 4
                  }
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)

        response.isEqualTo(CommonError.INVALID_IMAGE_EXTENSION.exception)
    }

    @Test
    fun `byteSize 가 50MB가 넘을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "image" : {
                    "base64Raw" : "test",
                    "extension" : "PNG",
                    "byteSize" : 51000000
                  }
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)

        response.isEqualTo(CommonError.INVALID_BYTE_SIZE.exception)
    }

    @Test
    fun `realName 이 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "bank" : "bank",
                  "accountNumber" : "accountNumber"
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.REQUIRED_REAL_NAME.exception)
    }

    @Test
    fun `realName 이 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "realName" : "",
                  "bank" : "bank",
                  "accountNumber" : "accountNumber"
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.REQUIRED_REAL_NAME.exception)
    }

    @Test
    fun `bank 가 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "realName" : "realName",
                  "accountNumber" : "accountNumber"
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.REQUIRED_BANK.exception)
    }

    @Test
    fun `bank 가 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "bank" : "",
                  "realName" : "realName",
                  "accountNumber" : "accountNumber"
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.REQUIRED_BANK.exception)
    }

    @Test
    fun `accountNumber 가 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "bank" : "bank",
                  "realName" : "realName"
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.REQUIRED_ACCOUNT_NUMBER.exception)
    }

    @Test
    fun `accountNumber 가 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = """
                {
                  "bank" : "bank",
                  "realName" : "realName",
                  "accountNumber" : ""
                }
            """.trimIndent()
        val (_, tokenGroup) = signUp()
        val response =
            webTestClient
                .patchForBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)

        response.isEqualTo(MemberError.REQUIRED_ACCOUNT_NUMBER.exception)
    }

    private suspend fun signUp(): Pair<SignUpResponse, TokenGroup> {
        val categoryId = categoryRepository.findAll().toList()[0].id!!.toString()
        val signUpRequest = SignUpRequest(
            oauthId = "oauthId",
            oauthType = "NAVER",
            nickname = "nickname",
            favoriteCategoryIdList = listOf(categoryId)
        )
        val signUpResponse = signUp(signUpRequest)
        val member = memberRepository.findById(signUpResponse.member.id)!!.toDomain()
        val tokenGroup = tokenService.createTokenGroup(member)
        return Pair(signUpResponse, tokenGroup)
    }

    private fun signUp(request: SignUpRequest) = webTestClient
        .post()
        .uri("/api/v1/members/signup")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody<SignUpResponse>()
        .returnResult()
        .responseBody!!

    private fun postModifyProfile(
        tokenGroup: TokenGroup,
        modifyProfileRequest: ModifyProfileRequest,
    ) {
        val response = webTestClient
            .patch()
            .uri("/api/v1/members/profile")
            .header(AUTHORIZATION, "Bearer ${tokenGroup.accessToken}")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(modifyProfileRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<ModifyProfileResponse>()
            .returnResult()
            .responseBody!!

        val memberInfo = response.memberInfo
        val favoriteCategories = response.favoriteCategoryIdList.map { it.toString() }

        assertThat(memberInfo.phoneNumber).isEqualTo(modifyProfileRequest.phoneNumber)
        assertThat(memberInfo.blogLink).isEqualTo(modifyProfileRequest.blogLink)
        assertThat(memberInfo.instagramLink).isEqualTo(modifyProfileRequest.instagramLink)
        assertThat(memberInfo.youtubeLink).isEqualTo(modifyProfileRequest.youtubeLink)
        assertThat(memberInfo.introduction).isEqualTo(modifyProfileRequest.introduction)
        assertThat(memberInfo.email).isEqualTo(modifyProfileRequest.email)
        assertThatList(favoriteCategories).isEqualTo(modifyProfileRequest.favoriteCategoryIdList)
    }
}
