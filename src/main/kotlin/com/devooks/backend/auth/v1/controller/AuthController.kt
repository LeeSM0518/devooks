package com.devooks.backend.auth.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.domain.OauthInfo
import com.devooks.backend.auth.v1.domain.TokenGroup
import com.devooks.backend.auth.v1.dto.LoginCommand
import com.devooks.backend.auth.v1.dto.LoginRequest
import com.devooks.backend.auth.v1.dto.LoginResponse
import com.devooks.backend.auth.v1.dto.LogoutCommand
import com.devooks.backend.auth.v1.dto.LogoutRequest
import com.devooks.backend.auth.v1.dto.LogoutResponse
import com.devooks.backend.auth.v1.dto.ReissueCommand
import com.devooks.backend.auth.v1.dto.ReissueRequest
import com.devooks.backend.auth.v1.dto.ReissueResponse
import com.devooks.backend.auth.v1.service.OauthService
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val oauthService: OauthService,
    private val tokenService: TokenService,
    private val memberService: MemberService,
) {

    @PostMapping("/login")
    @Operation(summary = "로그인")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-1 : 인증 코드(authorizationCode)가 NULL이거나 빈 문자일 경우\n" +
                        "- AUTH-400-2 : 인증 유형(oauthType)이 NAVER, KAKAO, GOOGLE 이 아닐 경우 ",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "401",
                description =
                "- AUTH-401-3 : 네이버 로그인을 실패할 경우\n" +
                        "- AUTH-401-4 : 카카오 로그인을 실패할 경우\n" +
                        "- AUTH-401-5 : 구글 로그인을 실패할 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- AUTH-403-1 : 정지된 회원일 경우 경우\n" +
                        "- AUTH-403-2 : 탈퇴한 회원일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- MEMBER-404-1 : 회원을 찾을 수 없는 경우 (message에 oauthId를 넣어서 응답)",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            )
        ]
    )
    suspend fun login(
        @RequestBody
        request: LoginRequest,
    ): LoginResponse {
        val command: LoginCommand = request.toCommand()
        val oauthInfo: OauthInfo = oauthService.getOauthInfo(command)
        val member: Member = memberService.findByOauthInfo(oauthInfo)
        val tokenGroup: TokenGroup = tokenService.createTokenGroup(member)
        return LoginResponse(member, tokenGroup)
    }

    @Transactional
    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-3 : 리프래시 토큰(refreshToken)이 NULL이거나 빈 문자일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "401",
                description =
                "- AUTH-401-1 : 기간이 만료된 토큰일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- AUTH-403-1 : 잘못된 형식의 토큰일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            )
        ]
    )
    suspend fun logout(
        @RequestBody
        request: LogoutRequest,
    ): LogoutResponse {
        val command: LogoutCommand = request.toCommand()
        val memberId: UUID = tokenService.getMemberId(Authorization(command.refreshToken))
        tokenService.expireRefreshToken(memberId)
        return LogoutResponse()
    }

    @Operation(summary = "토큰 재발급")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-3 : 리프래시 토큰(refreshToken)이 NULL이거나 빈 문자일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "401",
                description =
                "- AUTH-401-1 : 기간이 만료된 토큰일 경우\n" +
                        "- AUTH-401-2 : 원본 리프래시 토큰과 일치하지 않을 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- AUTH-403-1 : 잘못된 형식의 토큰일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- AUTH-404-1 : 리프래시 토큰이 존재하지 않을 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            )
        ]
    )
    @PostMapping("/reissue")
    suspend fun reissue(
        @RequestBody
        request: ReissueRequest,
    ): ReissueResponse {
        val command: ReissueCommand = request.toCommand()
        val tokenGroup: TokenGroup = tokenService.reissueTokenGroup(command.refreshToken)
        return ReissueResponse(tokenGroup)
    }

}