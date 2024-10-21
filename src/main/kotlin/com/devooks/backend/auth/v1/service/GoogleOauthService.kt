package com.devooks.backend.auth.v1.service

import com.devooks.backend.auth.v1.client.google.GoogleOauthClient
import com.devooks.backend.auth.v1.client.google.GoogleProfileClient
import com.devooks.backend.auth.v1.client.google.dto.GetGoogleTokenRequest
import com.devooks.backend.auth.v1.config.oauth.GoogleOauthProperties
import com.devooks.backend.auth.v1.domain.OauthGrantType
import com.devooks.backend.auth.v1.domain.OauthId
import com.devooks.backend.auth.v1.dto.LoginCommand
import com.devooks.backend.auth.v1.error.AuthError.FAILED_GOOGLE_OAUTH_LOGIN
import org.springframework.stereotype.Service

@Service
class GoogleOauthService(
    private val googleOauthClient: GoogleOauthClient,
    private val googleProfileClient: GoogleProfileClient,
    private val googleOauthProperties: GoogleOauthProperties,
) {

    fun getOauthId(request: LoginCommand): OauthId = getOauthId(getToken(request))

    private fun getOauthId(token: String): OauthId =
        googleProfileClient.getOauthId(token).id
            ?: throw FAILED_GOOGLE_OAUTH_LOGIN.exception

    private fun getToken(request: LoginCommand): String =
        googleOauthClient
            .getToken(
                GetGoogleTokenRequest(
                    grantType = OauthGrantType.AUTHORIZATION_CODE.value,
                    clientId = googleOauthProperties.clientId,
                    clientSecret = googleOauthProperties.clientSecret,
                    redirectUri = googleOauthProperties.redirectUri,
                    code = request.authorizationCode
                ).toString()
            )
            .takeIf { it.accessToken != null }
            ?.let { "Bearer ${it.accessToken}" }
            ?: throw FAILED_GOOGLE_OAUTH_LOGIN.exception

}
