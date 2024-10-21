package com.devooks.backend.auth.v1.client.kakao

import com.devooks.backend.auth.v1.client.kakao.dto.GetKakaoTokenResponse
import com.devooks.backend.auth.v1.config.feign.FormFeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(
    name = "kakaoOauthClient",
    url = "\${kakao.oauthHost}",
    configuration = [FormFeignConfig::class]
)
interface KakaoOauthClient {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["\${kakao.tokenUrl}"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
        headers = ["Content-Type: ${MediaType.APPLICATION_FORM_URLENCODED_VALUE}"]
    )
    fun getToken(request: String): GetKakaoTokenResponse
}