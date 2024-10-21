package com.devooks.backend.auth.v1.service

import com.devooks.backend.auth.v1.domain.OauthInfo
import com.devooks.backend.auth.v1.domain.OauthType.GOOGLE
import com.devooks.backend.auth.v1.domain.OauthType.KAKAO
import com.devooks.backend.auth.v1.domain.OauthType.NAVER
import com.devooks.backend.auth.v1.dto.LoginCommand
import com.devooks.backend.common.utils.logger
import org.springframework.stereotype.Service

@Service
class OauthService(
    private val naverOauthService: NaverOauthService,
    private val kakaoOauthService: KakaoOauthService,
    private val googleOauthService: GoogleOauthService,
) {
    private val logger = logger()

    suspend fun getOauthInfo(command: LoginCommand): OauthInfo =
        runCatching {
            when (command.oauthType) {
                NAVER -> OauthInfo(naverOauthService.getOauthId(command), NAVER)
                KAKAO -> OauthInfo(kakaoOauthService.getOauthId(command), KAKAO)
                GOOGLE -> OauthInfo(googleOauthService.getOauthId(command), GOOGLE)
            }
        }.getOrElse {
            logger.error(it.toString())
            logger.error(it.stackTraceToString())
            throw it
        }
}
