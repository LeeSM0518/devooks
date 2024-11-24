package com.devooks.backend.auth.v1.controller

import com.devooks.backend.auth.v1.client.naver.NaverOauthClient
import com.devooks.backend.auth.v1.client.naver.NaverProfileClient
import com.devooks.backend.auth.v1.client.naver.dto.GetNaverProfileResponse
import com.devooks.backend.auth.v1.client.naver.dto.GetNaverTokenResponse
import com.devooks.backend.auth.v1.config.oauth.NaverOauthProperties
import com.devooks.backend.auth.v1.domain.OauthGrantType
import com.devooks.backend.auth.v1.domain.OauthType
import com.devooks.backend.auth.v1.dto.LoginRequest
import com.devooks.backend.auth.v1.dto.LoginResponse
import com.devooks.backend.auth.v1.dto.LogoutRequest
import com.devooks.backend.auth.v1.dto.ReissueRequest
import com.devooks.backend.auth.v1.error.AuthError
import com.devooks.backend.auth.v1.repository.OauthInfoRepository
import com.devooks.backend.auth.v1.repository.RefreshTokenRepository
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.repository.CategoryRepository
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.fixture.ErrorResponse
import com.devooks.backend.fixture.ErrorResponse.Companion.isBadRequest
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.dto.SignUpRequest
import com.devooks.backend.member.v1.dto.SignUpResponse
import com.devooks.backend.member.v1.dto.WithdrawMemberRequest
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.error.MemberError
import com.devooks.backend.member.v1.repository.MemberRepository
import java.time.Instant
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
internal class AuthControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
    private val naverOauthProperties: NaverOauthProperties,
    private val oauthInfoRepository: OauthInfoRepository,
    private val categoryRepository: CategoryRepository,
    private val tokenService: TokenService,
) {

    lateinit var signUpRequest: SignUpRequest
    lateinit var member: MemberEntity

    @MockBean
    lateinit var naverOauthClient: NaverOauthClient

    @MockBean
    lateinit var naverProfileClient: NaverProfileClient

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        val categoryEntity = categoryRepository.findAll().toList()[0]
        signUpRequest = SignUpRequest(
            oauthId = "oauthId",
            oauthType = OauthType.NAVER,
            nickname = "nickname",
            favoriteCategoryIdList = listOf(categoryEntity.id!!)
        )
        val responseMember = webTestClient
            .post()
            .uri("/api/v1/members/signup")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .bodyValue(signUpRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody<SignUpResponse>()
            .returnResult()
            .responseBody!!
            .member
        member = memberRepository.findById(responseMember.id)!!
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        refreshTokenRepository.deleteAll()
        oauthInfoRepository.deleteAll()
        memberRepository.deleteAll()
    }

    @Test
    fun `로그인 할 수 있다`(): Unit = runBlocking {
        // given
        // when
        val response = postLogin()

        // then
        assertThat(response.member.id).isEqualTo(member.id)
    }

    @Test
    fun `로그인시 인증 코드가 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "oauthType" to "NAVER"
        )
        webTestClient.post().isBadRequest("/api/v1/auth/login", request)
    }

    @Test
    fun `로그인시 인증 코드가 비어 있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "authorizationCode" to "",
            "oauthType" to "NAVER"
        )
        webTestClient.post().isBadRequest("/api/v1/auth/login", request)
    }

    @Test
    fun `잘못된 인증 코드로 로그인을 실패할 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = LoginRequest("code", OauthType.NAVER)
        val getNaverTokenResponse =
            GetNaverTokenResponse(null, null, null, null, "error", "erorDescription")
        given(
            naverOauthClient.getToken(
                OauthGrantType.AUTHORIZATION_CODE.value,
                naverOauthProperties.clientId,
                naverOauthProperties.clientSecret,
                request.authorizationCode,
                state = naverOauthProperties.state
            )
        ).willReturn(getNaverTokenResponse)

        val error = webTestClient
            .post()
            .uri("/api/v1/auth/login")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody<ErrorResponse>()
            .returnResult()
            .responseBody!!

        assertThat(error.code).isEqualTo(AuthError.FAILED_NAVER_OAUTH_LOGIN.exception.code)
    }

    @Test
    fun `회원이 존재하지 않을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = LoginRequest("code", OauthType.NAVER)
        val getNaverTokenResponse =
            GetNaverTokenResponse("accessToken", "refreshToken", "tokenType", "expiresIn", null, null)
        given(
            naverOauthClient.getToken(
                OauthGrantType.AUTHORIZATION_CODE.value,
                naverOauthProperties.clientId,
                naverOauthProperties.clientSecret,
                request.authorizationCode,
                state = naverOauthProperties.state
            )
        ).willReturn(getNaverTokenResponse)

        val oauthId = "wrong"
        given(naverProfileClient.getOauthId(getNaverTokenResponse.token!!))
            .willReturn(
                GetNaverProfileResponse(
                    "resultCode",
                    "message",
                    GetNaverProfileResponse.Profile(oauthId, oauthId)
                )
            )

        val responseBody = webTestClient
            .post()
            .uri("/api/v1/auth/login")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
            .expectBody<Map<String, Any>>()
            .returnResult()
            .responseBody!!

        val message = responseBody["message"] as Map<*, *>
        assertThat(message["oauthId"]).isEqualTo(oauthId)
    }

    @Test
    fun `정지 당한 회원일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = LoginRequest("code", OauthType.NAVER)
        val getNaverTokenResponse =
            GetNaverTokenResponse("accessToken", "refreshToken", "tokenType", "expiresIn", null, null)
        memberRepository.save(member.copy(untilSuspensionDate = Instant.now().plusSeconds(60L)))
        given(
            naverOauthClient.getToken(
                OauthGrantType.AUTHORIZATION_CODE.value,
                naverOauthProperties.clientId,
                naverOauthProperties.clientSecret,
                request.authorizationCode,
                state = naverOauthProperties.state
            )
        ).willReturn(getNaverTokenResponse)

        given(naverProfileClient.getOauthId(getNaverTokenResponse.token!!))
            .willReturn(
                GetNaverProfileResponse(
                    "resultCode",
                    "message",
                    GetNaverProfileResponse.Profile(signUpRequest.oauthId, "email")
                )
            )

        val code = webTestClient
            .post()
            .uri("/api/v1/auth/login")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isForbidden
            .expectBody<ErrorResponse>()
            .returnResult()
            .responseBody!!
            .code

        assertThat(code).isEqualTo(MemberError.SUSPENDED_MEMBER.exception.code)
    }

    @Test
    fun `탈퇴한 회원일 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = LoginRequest("code", OauthType.NAVER)
        val getNaverTokenResponse =
            GetNaverTokenResponse("accessToken", "refreshToken", "tokenType", "expiresIn", null, null)
        given(
            naverOauthClient.getToken(
                OauthGrantType.AUTHORIZATION_CODE.value,
                naverOauthProperties.clientId,
                naverOauthProperties.clientSecret,
                request.authorizationCode,
                state = naverOauthProperties.state
            )
        ).willReturn(getNaverTokenResponse)

        given(naverProfileClient.getOauthId(getNaverTokenResponse.token!!))
            .willReturn(
                GetNaverProfileResponse(
                    "resultCode",
                    "message",
                    GetNaverProfileResponse.Profile(signUpRequest.oauthId, "email")
                )
            )

        val accessToken = tokenService.createTokenGroup(member.toDomain()).accessToken

        webTestClient
            .patch()
            .uri("/api/v1/members/withdrawal")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .bodyValue(WithdrawMemberRequest(withdrawalReason = "reason"))
            .header(AUTHORIZATION, "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk

        val code = webTestClient
            .post()
            .uri("/api/v1/auth/login")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isForbidden
            .expectBody<ErrorResponse>()
            .returnResult()
            .responseBody!!
            .code

        assertThat(code).isEqualTo(MemberError.WITHDREW_MEMBER.exception.code)
    }

    @Test
    fun `로그아웃 할 수 있다`(): Unit = runBlocking {
        // given
        val loginResponse = postLogin()
        val logoutRequest = LogoutRequest(loginResponse.tokenGroup.refreshToken)

        // when
        // then
        webTestClient
            .post()
            .uri("/api/v1/auth/logout")
            .contentType(APPLICATION_JSON)
            .bodyValue(logoutRequest)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `토큰을 재발급 받을 수 있다`(): Unit = runBlocking {
        // given
        val loginResponse = postLogin()
        val reissueRequest = ReissueRequest(loginResponse.tokenGroup.refreshToken)

        // when
        webTestClient
            .post()
            .uri("/api/v1/auth/reissue")
            .contentType(APPLICATION_JSON)
            .bodyValue(reissueRequest)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `authorizationCode가 존재하지 않을 경우 로그인시 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf("oauthType" to "NAVER")

        webTestClient.post().isBadRequest("/api/v1/auth/login", request)
    }

    @Test
    fun `authorizationCode가 비어있을 경우 로그인시 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "authorizationCode" to "",
            "oauthType" to "NAVER"
        )

        webTestClient.post().isBadRequest("/api/v1/auth/login", request)
    }

    @Test
    fun `oauthType이 존재하지 않을 경우 로그인시 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "authorizationCode" to "code",
        )
        webTestClient.post().isBadRequest("/api/v1/auth/login", request)
    }

    @Test
    fun `oauthType이 값이 NAVER, GOOGLE, KAKAO가 아닐 경우 로그인시 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "authorizationCode" to "code",
            "oauthType" to "NAVERR"
        )
        webTestClient.post().isBadRequest("/api/v1/auth/login", request)
    }

    @Test
    fun `refreshToken이 존재하지 않을 경우 로그아웃시 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf<String, Any>()
        webTestClient.post().isBadRequest("/api/v1/auth/logout", request)
    }

    @Test
    fun `refreshToken이 비어있을 경우 로그아웃시 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "refreshToken" to "",
        )
        webTestClient.post().isBadRequest("/api/v1/auth/logout", request)
    }

    @Test
    fun `토큰 재발급시 refreshToken이 비어있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "refreshToken" to "",
        )
        webTestClient.post().isBadRequest("/api/v1/auth/reissue", request)
    }

    @Test
    fun `이메일 확인시 email 형식이 잘못되어 있을 경우 예외가 발생한다`(): Unit = runBlocking {
        val request = mapOf(
            "email" to "asd@asd",
        )
        webTestClient.post().isBadRequest("/api/v1/auth/check/email", request)
    }

    private fun postLogin(): LoginResponse {
        val request = LoginRequest("code", OauthType.NAVER)
        val getNaverTokenResponse =
            GetNaverTokenResponse("accessToken", "refreshToken", "tokenType", "expiresIn", null, null)
        given(
            naverOauthClient.getToken(
                OauthGrantType.AUTHORIZATION_CODE.value,
                naverOauthProperties.clientId,
                naverOauthProperties.clientSecret,
                request.authorizationCode,
                state = naverOauthProperties.state
            )
        ).willReturn(getNaverTokenResponse)
        given(naverProfileClient.getOauthId(getNaverTokenResponse.token!!))
            .willReturn(
                GetNaverProfileResponse(
                    "resultCode",
                    "message",
                    GetNaverProfileResponse.Profile(signUpRequest.oauthId, "email")
                )
            )

        val response = webTestClient
            .post()
            .uri("/api/v1/auth/login")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody<LoginResponse>()
            .returnResult()
            .responseBody!!
        return response
    }
}
