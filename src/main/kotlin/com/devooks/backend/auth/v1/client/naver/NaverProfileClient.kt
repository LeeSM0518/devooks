package com.devooks.backend.auth.v1.client.naver

import com.devooks.backend.auth.v1.client.naver.dto.GetNaverProfileResponse
import com.devooks.backend.auth.v1.config.feign.BasicFeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(
    name = "naverProfileClient",
    url = "\${naver.profileHost}",
    configuration = [BasicFeignConfig::class]
)
interface NaverProfileClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["\${naver.profileUrl}"])
    fun getOauthId(@RequestHeader("Authorization") token: String): GetNaverProfileResponse
}