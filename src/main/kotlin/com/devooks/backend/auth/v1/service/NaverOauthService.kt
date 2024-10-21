package com.devooks.backend.auth.v1.service

import com.devooks.backend.auth.v1.client.naver.NaverOauthClient
import com.devooks.backend.auth.v1.client.naver.NaverProfileClient
import com.devooks.backend.auth.v1.config.oauth.NaverOauthProperties
import com.devooks.backend.auth.v1.domain.OauthGrantType
import com.devooks.backend.auth.v1.domain.OauthId
import com.devooks.backend.auth.v1.dto.LoginCommand
import com.devooks.backend.auth.v1.error.AuthError
import org.springframework.stereotype.Service

@Service
class NaverOauthService(
    private val naverOauthClient: NaverOauthClient,
    private val naverProfileClient: NaverProfileClient,
    private val naverOauthProperties: NaverOauthProperties,
) {

    fun getOauthId(command: LoginCommand): OauthId = getOauthId(getToken(command))

    private fun getOauthId(token: String): OauthId =
        naverProfileClient.getOauthId(token).profile?.id
            ?: throw AuthError.FAILED_NAVER_OAUTH_LOGIN.exception

    private fun getToken(command: LoginCommand): String =
        naverOauthClient
            .getToken(
                grantType = OauthGrantType.AUTHORIZATION_CODE.value,
                clientId = naverOauthProperties.clientId,
                clientSecret = naverOauthProperties.clientSecret,
                code = command.authorizationCode,
                state = naverOauthProperties.state
            )
            .takeIf { it.token  != null}
            ?.token
            ?: throw AuthError.FAILED_NAVER_OAUTH_LOGIN.exception
}