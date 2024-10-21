package com.devooks.backend.auth.v1.client.naver

import com.devooks.backend.auth.v1.client.naver.dto.GetNaverTokenResponse
import com.devooks.backend.auth.v1.config.feign.BasicFeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "naverOauthClient",
    url = "\${naver.oauthHost}",
    configuration = [BasicFeignConfig::class]
)
interface NaverOauthClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["\${naver.tokenUrl}"])
    fun getToken(
        @RequestParam("grant_type") grantType: String,
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("code") code: String,
        @RequestParam("state") state: String,
    ): GetNaverTokenResponse
}