package com.devooks.backend.auth.v1.service

import com.devooks.backend.auth.v1.config.JwtConfigProperties
import com.devooks.backend.auth.v1.domain.AccessToken
import com.devooks.backend.auth.v1.domain.RefreshToken
import com.devooks.backend.auth.v1.domain.TokenSubject
import com.devooks.backend.auth.v1.entity.RefreshTokenEntity
import com.devooks.backend.auth.v1.error.AuthError.EXPIRED_TOKEN
import com.devooks.backend.auth.v1.error.AuthError.FAILED_CREATE_ACCESS_TOKEN
import com.devooks.backend.auth.v1.error.AuthError.FAILED_CREATE_REFRESH_TOKEN
import com.devooks.backend.auth.v1.error.AuthError.FAILED_VALIDATE_TOKEN
import com.devooks.backend.auth.v1.error.AuthError.INVALID_REFRESH_TOKEN
import com.devooks.backend.auth.v1.error.AuthError.NOT_FOUND_REFRESH_TOKEN
import com.devooks.backend.auth.v1.error.AuthError.UNSUPPORTED_TOKEN_FORMAT
import com.devooks.backend.auth.v1.repository.RefreshTokenRepository
import com.devooks.backend.common.utils.logger
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JwtService(
    private val refreshTokenRepository: RefreshTokenRepository,
    jwtConfigProperties: JwtConfigProperties,
) {
    private val logger = logger()

    private val secretKey = jwtConfigProperties.secretKey.toByteArray()
    private val accessTokenExpirationHour = jwtConfigProperties.accessTokenExpirationHour.toLong()
    private val refreshTokenExpirationHour = jwtConfigProperties.refreshTokenExpirationHour.toLong()

    suspend fun createAccessToken(memberId: UUID): AccessToken =
        runCatching {
            createToken(memberId, accessTokenExpirationHour)
        }.getOrElse {
            logger.error(FAILED_CREATE_ACCESS_TOKEN.toString())
            logger.error(it.stackTraceToString())
            throw FAILED_CREATE_ACCESS_TOKEN.exception
        }

    @Transactional
    suspend fun createRefreshToken(memberId: UUID): RefreshToken =
        runCatching {
            val token = createToken(memberId, refreshTokenExpirationHour)
            refreshTokenRepository
                .findByMemberId(memberId)
                .let { refreshToken ->
                    refreshToken
                        ?.update(token)
                        ?: RefreshTokenEntity(memberId = memberId, token = token)
                }
                .let { refreshToken -> refreshTokenRepository.save(refreshToken) }
            token
        }.getOrElse {
            logger.error(FAILED_CREATE_REFRESH_TOKEN.toString())
            logger.error(it.stackTraceToString())
            throw FAILED_CREATE_REFRESH_TOKEN.exception
        }

    suspend fun validateToken(token: AccessToken): TokenSubject = validateToken(secretKey, token)

    suspend fun validateRefreshToken(token: RefreshToken): TokenSubject =
        runCatching {
            val tokenSubject = validateToken(secretKey, token)
            validateRefreshToken(tokenSubject, token)
            tokenSubject
        }.getOrElse { exception ->
            logger.error("리프래시 토큰 검증을 실패했습니다.")
            logger.error(exception.stackTraceToString())
            throw exception
        }

    suspend fun expireRefreshToken(memberId: UUID) {
        refreshTokenRepository.deleteByMemberId(memberId)
    }

    private fun validateToken(secretKey: ByteArray, token: String): TokenSubject =
        runCatching {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey))
                .build()
                .parseClaimsJws(token)
                .body
                .subject
                .let { memberId -> TokenSubject(UUID.fromString(memberId)) }
        }.getOrElse { exception ->
            val error = when (exception) {
                is UnsupportedJwtException,
                is MalformedJwtException,
                is SignatureException,
                is IllegalArgumentException,
                -> UNSUPPORTED_TOKEN_FORMAT

                is ExpiredJwtException -> EXPIRED_TOKEN

                else -> FAILED_VALIDATE_TOKEN
            }
            logger.error(error.toString())
            logger.error(exception.stackTraceToString())
            throw error.exception
        }

    private suspend fun validateRefreshToken(
        tokenSubject: TokenSubject,
        token: RefreshToken,
    ) {
        val foundRefreshToken: RefreshTokenEntity =
            refreshTokenRepository
                .findByMemberId(tokenSubject.memberId)
                ?: throw NOT_FOUND_REFRESH_TOKEN.exception

        foundRefreshToken
            .takeIf { it.token == token }
            ?: throw INVALID_REFRESH_TOKEN.exception
    }

    private fun createToken(memberId: UUID, expirationHour: Long): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .setSubject(memberId.toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(expirationHour, ChronoUnit.HOURS)))
            .signWith(Keys.hmacShaKeyFor(secretKey), SignatureAlgorithm.HS256)
            .compact()
    }


}