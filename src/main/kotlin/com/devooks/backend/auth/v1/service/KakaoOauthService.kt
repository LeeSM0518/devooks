package com.devooks.backend.auth.v1.service

import com.devooks.backend.auth.v1.client.kakao.KakaoOauthClient
import com.devooks.backend.auth.v1.client.kakao.KakaoProfileClient
import com.devooks.backend.auth.v1.client.kakao.dto.GetKakaoTokenRequest
import com.devooks.backend.auth.v1.config.oauth.KakaoOauthProperties
import com.devooks.backend.auth.v1.domain.OauthGrantType
import com.devooks.backend.auth.v1.domain.OauthId
import com.devooks.backend.auth.v1.dto.LoginCommand
import com.devooks.backend.auth.v1.error.AuthError.FAILED_KAKAO_OAUTH_LOGIN
import org.springframework.stereotype.Service

@Service
class KakaoOauthService(
    private val kakaoOauthClient: KakaoOauthClient,
    private val kakaoProfileClient: KakaoProfileClient,
    private val kakaoOauthProperties: KakaoOauthProperties,
) {

    fun getOauthId(request: LoginCommand): OauthId = getOauthId(getToken(request))

    private fun getOauthId(token: String): OauthId =
        kakaoProfileClient.getOauthId(token).id?.toString()
            ?: throw FAILED_KAKAO_OAUTH_LOGIN.exception

    private fun getToken(request: LoginCommand): String =
        kakaoOauthClient
            .getToken(
                GetKakaoTokenRequest(
                    grantType = OauthGrantType.AUTHORIZATION_CODE.value,
                    clientId = kakaoOauthProperties.clientId,
                    redirectUri = kakaoOauthProperties.redirectUri,
                    code = request.authorizationCode,
                ).toString()
            )
            .takeIf { it.accessToken != null }
            ?.let { "Bearer ${it.accessToken}" }
            ?: throw FAILED_KAKAO_OAUTH_LOGIN.exception
}