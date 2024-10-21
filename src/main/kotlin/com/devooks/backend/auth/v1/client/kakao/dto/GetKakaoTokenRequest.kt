package com.devooks.backend.auth.v1.client.kakao.dto

class GetKakaoTokenRequest(
    val grantType: String,
    val clientId: String,
    val redirectUri: String,
    val code: String,
) {
    override fun toString(): String {
        return "grant_type=${grantType}&client_id=${clientId}&redirect_uri=${redirectUri}&code=${code}"
    }
}
