package com.devooks.backend.auth.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.domain.OauthInfo
import com.devooks.backend.auth.v1.domain.TokenGroup
import com.devooks.backend.auth.v1.dto.CheckEmailCommand
import com.devooks.backend.auth.v1.dto.CheckEmailRequest
import com.devooks.backend.auth.v1.dto.CheckEmailResponse
import com.devooks.backend.auth.v1.dto.LoginCommand
import com.devooks.backend.auth.v1.dto.LoginRequest
import com.devooks.backend.auth.v1.dto.LoginResponse
import com.devooks.backend.auth.v1.dto.LogoutCommand
import com.devooks.backend.auth.v1.dto.LogoutRequest
import com.devooks.backend.auth.v1.dto.LogoutResponse
import com.devooks.backend.auth.v1.dto.ReissueCommand
import com.devooks.backend.auth.v1.dto.ReissueRequest
import com.devooks.backend.auth.v1.dto.ReissueResponse
import com.devooks.backend.auth.v1.service.MailService
import com.devooks.backend.auth.v1.service.OauthService
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.service.MemberService
import java.util.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val oauthService: OauthService,
    private val tokenService: TokenService,
    private val memberService: MemberService,
    private val mailService: MailService,
): AuthControllerDocs {

    @PostMapping("/login")
    override suspend fun login(
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
    override suspend fun logout(
        @RequestBody
        request: LogoutRequest,
    ): LogoutResponse {
        val command: LogoutCommand = request.toCommand()
        val memberId: UUID = tokenService.getMemberId(Authorization(command.refreshToken))
        tokenService.expireRefreshToken(memberId)
        return LogoutResponse()
    }

    @PostMapping("/reissue")
    override suspend fun reissue(
        @RequestBody
        request: ReissueRequest,
    ): ReissueResponse {
        val command: ReissueCommand = request.toCommand()
        val tokenGroup: TokenGroup = tokenService.reissueTokenGroup(command.refreshToken)
        return ReissueResponse(tokenGroup)
    }

    @PostMapping("/check/email")
    override suspend fun checkEmail(
        @RequestBody
        request: CheckEmailRequest,
    ): CheckEmailResponse {
        val command: CheckEmailCommand = request.toCommand()
        mailService.sendCheckMessage(command)
        return CheckEmailResponse()
    }

}
