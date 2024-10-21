package com.devooks.backend.auth.v1.service

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.domain.RefreshToken
import com.devooks.backend.auth.v1.domain.TokenGroup
import com.devooks.backend.member.v1.domain.Member
import java.util.*
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val jwtService: JwtService,
) {

    suspend fun createTokenGroup(member: Member): TokenGroup =
        TokenGroup(
            accessToken = jwtService.createAccessToken(member.id),
            refreshToken = jwtService.createRefreshToken(member.id)
        )

    suspend fun reissueTokenGroup(refreshToken: RefreshToken): TokenGroup {
        val tokenSubject = jwtService.validateRefreshToken(refreshToken)
        val reissuedAccessToken = jwtService.createAccessToken(tokenSubject.memberId)
        val reissuedRefreshToken = jwtService.createRefreshToken(tokenSubject.memberId)
        return TokenGroup(reissuedAccessToken, reissuedRefreshToken)
    }

    suspend fun expireRefreshToken(memberId: UUID) {
        jwtService.expireRefreshToken(memberId)
    }

    suspend fun getMemberId(authorization: Authorization): UUID =
        jwtService
            .validateToken(authorization.token)
            .memberId

}
