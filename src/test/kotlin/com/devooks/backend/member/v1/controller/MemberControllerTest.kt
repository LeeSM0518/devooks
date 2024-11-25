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
import com.devooks.backend.common.domain.ImageExtension
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.fixture.ErrorResponse
import com.devooks.backend.fixture.ErrorResponse.Companion.isBadRequest
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.dto.GetProfileResponse
import com.devooks.backend.member.v1.dto.ModifyAccountInfoRequest
import com.devooks.backend.member.v1.dto.ModifyAccountInfoResponse
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
import java.time.Instant
import java.util.*
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
        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER,
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
        assertThat(category.id).isEqualTo(request.favoriteCategoryIdList.first())

        val favoriteCategory = favoriteCategoryRepository.findAll().firstOrNull()!!
        assertThat(favoriteCategory.categoryId).isEqualTo(category.id)
        assertThat(favoriteCategory.favoriteMemberId).isEqualTo(response.member.id)

        val refreshToken = refreshTokenRepository.findAll().firstOrNull()!!
        assertThat(refreshToken.memberId).isEqualTo(response.member.id)
        assertThat(refreshToken.token).isEqualTo(response.tokenGroup.refreshToken)
    }

    @Test
    fun `oauthId가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthType" to "NAVER",
            "nickname" to "nickname",
            "favoriteCategories" to UUID.randomUUID()
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `oauthId가 빈문자열인 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "",
            "oauthType" to "NAVER",
            "nickname" to "nickname",
            "favoriteCategories" to UUID.randomUUID()
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `oauthType이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "123",
            "nickname" to "nickname",
            "favoriteCategories" to UUID.randomUUID()
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `oauthType이 잘못된 형식일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "123",
            "oauthType" to "test",
            "nickname" to "nickname",
            "favoriteCategories" to UUID.randomUUID()
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `nickname 이 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "123",
            "oauthType" to "NAVER",
            "favoriteCategories" to UUID.randomUUID()
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `nickname 이 2자 미만일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "123",
            "oauthType" to "NAVER",
            "nickname" to "1",
            "favoriteCategories" to UUID.randomUUID()
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `nickname 이 12자 초과일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "123",
            "oauthType" to "NAVER",
            "nickname" to "1111111111111",
            "favoriteCategories" to UUID.randomUUID()
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `favoriteCategories 가 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "123",
            "oauthType" to "NAVER",
            "nickname" to "nickname",
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `favoriteCategories 가 UUID가 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthId" to "123",
            "oauthType" to "NAVER",
            "nickname" to "nickname",
            "favoriteCategories" to "test"
        )
        webTestClient.post().isBadRequest("/api/v1/members/signup", request)
    }

    @Test
    fun `닉네임이 이미 존재할 경우 회원가입 실패`(): Unit = runBlocking {
        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER,
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
        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER,
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
        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER,
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
        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val request = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER,
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
            realName = "1",
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
    fun `계좌정보에서 이름만 수정할 수 있다`(): Unit = runBlocking {
        val (signUpResponse, tokenGroup) = signUp()
        val modifyAccountInfoRequest = ModifyAccountInfoRequest(
            realName = "1111111111",
            bank = null,
            accountNumber = null,
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

        assertThat(response.realName).isEqualTo(memberInfo.realName)
        assertThat(response.bank).isNotNull()
        assertThat(response.accountNumber).isNotNull()
    }

    @Test
    fun `계좌번호에 특수문자가 입력될 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "realName" to "123",
            "bank" to "bank",
            "accountNumber" to "1234-1234-1234"
        )

        webTestClient.patch().isBadRequest("/api/v1/members/account", request)
    }

    @Test
    fun `프로필 사진을 수정할 수 있다`(): Unit = runBlocking {
        val (signUpResponse, tokenGroup) = signUp()
        val modifyProfileImageRequest = ModifyProfileImageRequest(
            image = ImageDto(
                "test",
                ImageExtension.PNG,
                4,
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
        assertThat(File(member.profileImagePath.substring(1)).exists()).isEqualTo(true)
    }

    @Test
    fun `프로필을 수정할 수 있다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val modifyProfileRequest =
            ModifyProfileRequest(
                nickname = "newNickname",
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
    fun `이메일만 수정할 수 있다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val modifyProfileRequest =
            ModifyProfileRequest(
                nickname = null,
                phoneNumber = null,
                blogLink = null,
                instagramLink = null,
                youtubeLink = null,
                introduction = null,
                favoriteCategoryIdList = null,
                email = "asd@naver.com"
            )

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

        val memberInfo = response.profile

        assertThat(memberInfo.email).isEqualTo(modifyProfileRequest.email)
    }

    @Test
    fun `전화번호 수정시 전화번호가 잘못되어 있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = mapOf(
            "phoneNumber" to "q-1234-1234"
        )
        webTestClient.patch().isBadRequest("/api/v1/members/profile", request, tokenGroup.accessToken)
    }

    @Test
    fun `닉네임 수정시 12 글자를 넘을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = mapOf("nickname" to "".plus("a").repeat(13))
        webTestClient.patch().isBadRequest("/api/v1/members/profile", request, tokenGroup.accessToken)
    }

    @Test
    fun `링크 수정시 255 글자를 넘을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = mapOf("blogLink" to "".plus("a").repeat(256))
        webTestClient.patch().isBadRequest("/api/v1/members/profile", request, tokenGroup.accessToken)
    }

    @Test
    fun `이메일 수정시 이메일이 잘못되어 있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = mapOf(
            "email" to "test@asd"
        )
        webTestClient.patch().isBadRequest("/api/v1/members/profile", request, tokenGroup.accessToken)
    }

    @Test
    fun `소개글 수정시 5000 글자가 넘을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = mapOf("introduction" to "".plus("a").repeat(5001))
        webTestClient.patch().isBadRequest("/api/v1/members/profile", request, tokenGroup.accessToken)
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

        val categoryId = categoryRepository.findAll().toList()[0].toDomain().toDto().id
        val foundMemberInfo = memberInfoRepository.findByMemberId(signUpResponse.member.id)!!

        assertThat(response.profile.id).isEqualTo(signUpResponse.member.id)
        assertThat(response.profile.nickname).isEqualTo(signUpResponse.member.nickname)
        assertThat(response.profile.profileImagePath).isEqualTo(signUpResponse.member.profileImagePath)
        assertThat(response.profile.favoriteCategoryIdList.first()).isEqualTo(categoryId)
        assertThat(response.profile.blogLink).isEqualTo(foundMemberInfo.blogLink)
        assertThat(response.profile.youtubeLink).isEqualTo(foundMemberInfo.youtubeLink)
        assertThat(response.profile.introduction).isEqualTo(foundMemberInfo.introduction)
        assertThat(response.profile.instagramLink).isEqualTo(foundMemberInfo.instagramLink)
        assertThat(response.profile.realName).isNull()
        assertThat(response.profile.bank).isNull()
        assertThat(response.profile.accountNumber).isNull()
        assertThat(response.profile.phoneNumber).isNull()
        assertThat(response.profile.email).isNull()
    }

    @Test
    fun `프로필 조회시 회원 식별자가 유효하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        webTestClient
            .get()
            .uri("/api/v1/members/test/profile")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `자신의 프로필을 조회할 경우 개인 정보를 확인할 수 있다`(): Unit = runBlocking {
        val (signUpResponse, _) = signUp()
        val member = memberRepository.findById(signUpResponse.member.id)!!.toDomain()
        val tokenGroup = tokenService.createTokenGroup(member)

        val response = webTestClient
            .get()
            .uri("/api/v1/members/${signUpResponse.member.id}/profile")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, "Bearer ${tokenGroup.accessToken}")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetProfileResponse>()
            .returnResult()
            .responseBody!!

        val categoryId = categoryRepository.findAll().toList()[0].toDomain().toDto().id
        val foundMemberInfo = memberInfoRepository.findByMemberId(signUpResponse.member.id)!!

        assertThat(response.profile.id).isEqualTo(signUpResponse.member.id)
        assertThat(response.profile.nickname).isEqualTo(signUpResponse.member.nickname)
        assertThat(response.profile.profileImagePath).isEqualTo(signUpResponse.member.profileImagePath)
        assertThat(response.profile.favoriteCategoryIdList.first()).isEqualTo(categoryId)
        assertThat(response.profile.blogLink).isEqualTo(foundMemberInfo.blogLink)
        assertThat(response.profile.youtubeLink).isEqualTo(foundMemberInfo.youtubeLink)
        assertThat(response.profile.introduction).isEqualTo(foundMemberInfo.introduction)
        assertThat(response.profile.instagramLink).isEqualTo(foundMemberInfo.instagramLink)
        assertThat(response.profile.realName).isEqualTo(foundMemberInfo.realName)
        assertThat(response.profile.bank).isEqualTo(foundMemberInfo.bank)
        assertThat(response.profile.accountNumber).isEqualTo(foundMemberInfo.accountNumber)
        assertThat(response.profile.phoneNumber).isEqualTo(foundMemberInfo.phoneNumber)
        assertThat(response.profile.email).isEqualTo(foundMemberInfo.email)
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
    fun `회원 탈퇴시 탈퇴 사유가 비어 있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = mapOf(
            "withdrawalReason" to ""
        )
        webTestClient.patch().isBadRequest("/api/v1/members/withdrawal", request, tokenGroup.accessToken)
    }

    @Test
    fun `회원 탈퇴시 탈퇴 사유가 255 글자를 넘을 경우 예외가 발생한다`(): Unit = runBlocking {
        val (_, tokenGroup) = signUp()
        val request = mapOf(
            "withdrawalReason" to "".plus("a").repeat(256)
        )
        webTestClient.patch().isBadRequest("/api/v1/members/withdrawal", request, tokenGroup.accessToken)
    }

    @Test
    fun `image 가 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf<String, Any>()
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)
    }

    @Test
    fun `base64Raw 가 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "image" to mapOf(
                "base64Raw" to "",
                "extension" to "PNG",
                "byteSize" to 4
            )
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)
    }

    @Test
    fun `extension 이 JPG, PNG, JPEG 이 아닐 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "image" to mapOf(
                "base64Raw" to "asdf",
                "extension" to "asd",
                "byteSize" to 4
            )
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)
    }

    @Test
    fun `byteSize 가 50MB가 넘을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "image" to mapOf(
                "base64Raw" to "1234",
                "extension" to "PNG",
                "byteSize" to 51_000_000
            )
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/image", request, tokenGroup.accessToken)
    }

    @Test
    fun `realName 이 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "bank" to "bank",
            "accountNumber" to "accountNumber"
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)
    }

    @Test
    fun `realName 이 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "realName" to "",
            "bank" to "bank",
            "accountNumber" to "accountNumber"
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)
    }

    @Test
    fun `bank 가 null 일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "realName" to "realName",
            "accountNumber" to "accountNumber"
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)
    }

    @Test
    fun `bank 가 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "realName" to "realName",
            "bank" to "",
            "accountNumber" to "accountNumber"
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)
    }

    @Test
    fun `accountNumber 가 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "realName" to "realName",
            "bank" to "bank",
            "accountNumber" to ""
        )
        val (_, tokenGroup) = signUp()
        webTestClient.patch().isBadRequest("/api/v1/members/account", request, tokenGroup.accessToken)
    }

    private suspend fun signUp(): Pair<SignUpResponse, TokenGroup> {
        val categoryId = categoryRepository.findAll().toList()[0].id!!
        val signUpRequest = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER,
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

        val memberInfo = response.profile
        val favoriteCategories = response.profile.favoriteCategoryIdList

        assertThat(memberInfo.nickname).isEqualTo(modifyProfileRequest.nickname)
        assertThat(memberInfo.phoneNumber).isEqualTo(modifyProfileRequest.phoneNumber)
        assertThat(memberInfo.blogLink).isEqualTo(modifyProfileRequest.blogLink)
        assertThat(memberInfo.instagramLink).isEqualTo(modifyProfileRequest.instagramLink)
        assertThat(memberInfo.youtubeLink).isEqualTo(modifyProfileRequest.youtubeLink)
        assertThat(memberInfo.introduction).isEqualTo(modifyProfileRequest.introduction)
        assertThat(memberInfo.email).isEqualTo(modifyProfileRequest.email)
        assertThatList(favoriteCategories).isEqualTo(modifyProfileRequest.favoriteCategoryIdList)
    }
}
