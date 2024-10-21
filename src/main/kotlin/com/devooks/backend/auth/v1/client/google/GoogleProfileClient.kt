package com.devooks.backend.auth.v1.client.google

import com.devooks.backend.auth.v1.client.google.dto.GetGoogleProfileResponse
import com.devooks.backend.auth.v1.config.feign.BasicFeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@FeignClient(
    name = "googleProfileClient",
    url = "\${google.profileHost}",
    configuration = [BasicFeignConfig::class]
)
interface GoogleProfileClient {
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["\${google.profileUrl}"],
    )
    fun getOauthId(
        @RequestHeader("Authorization") token: String
    ): GetGoogleProfileResponse
}
