package com.devooks.backend.auth.v1.error

import com.devooks.backend.auth.v1.domain.OauthType
import com.devooks.backend.common.error.validateNotBlank


fun String?.validateOauthId(): String =
    validateNotBlank(AuthError.REQUIRED_OAUTH_ID.exception)

fun String?.validateOauthType(): OauthType =
    validateNotBlank(AuthError.INVALID_OAUTH_TYPE.exception)
        .let { runCatching { OauthType.valueOf(it) }.getOrElse { null } }
        ?: throw AuthError.INVALID_OAUTH_TYPE.exception

fun String?.validateAuthorizationCode(): String =
    validateNotBlank(AuthError.REQUIRED_AUTHORIZATION_CODE.exception)

fun String?.validateRefreshToken(): String =
    validateNotBlank(AuthError.REQUIRED_TOKEN.exception)