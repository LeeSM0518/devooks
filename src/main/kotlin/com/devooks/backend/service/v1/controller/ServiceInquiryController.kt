package com.devooks.backend.service.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.service.v1.domain.ServiceInquiry
import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import com.devooks.backend.service.v1.dto.command.CreateServiceInquiryCommand
import com.devooks.backend.service.v1.dto.command.GetServiceInquiriesCommand
import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import com.devooks.backend.service.v1.dto.request.CreateServiceInquiryRequest
import com.devooks.backend.service.v1.dto.request.ModifyServiceInquiryRequest
import com.devooks.backend.service.v1.dto.response.CreateServiceInquiryResponse
import com.devooks.backend.service.v1.dto.response.GetServiceInquiriesResponse
import com.devooks.backend.service.v1.dto.response.GetServiceInquiriesResponse.Companion.toGetServiceInquiriesResponse
import com.devooks.backend.service.v1.dto.response.ModifyServiceInquiryResponse
import com.devooks.backend.service.v1.dto.response.ServiceInquiryResponse
import com.devooks.backend.service.v1.service.ServiceInquiryImageService
import com.devooks.backend.service.v1.service.ServiceInquiryService
import java.util.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/service-inquiries")
class ServiceInquiryController(
    private val tokenService: TokenService,
    private val serviceInquiryService: ServiceInquiryService,
    private val serviceInquiryImageService: ServiceInquiryImageService,
) {

    @Transactional
    @PostMapping
    suspend fun createServiceInquiry(
        @RequestBody
        request: CreateServiceInquiryRequest,
        @RequestHeader(AUTHORIZATION, required = false, defaultValue = "")
        authorization: String,
    ): CreateServiceInquiryResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: CreateServiceInquiryCommand = request.toCommand(requesterId)
        val serviceInquiry: ServiceInquiry = serviceInquiryService.create(command)
        val serviceInquiryImageList: List<ServiceInquiryImage> =
            command.imageIdList?.let { serviceInquiryImageService.save(it, serviceInquiry) } ?: listOf()
        return CreateServiceInquiryResponse(ServiceInquiryResponse(serviceInquiry, serviceInquiryImageList))
    }

    @GetMapping
    suspend fun getServiceInquiries(
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
        @RequestHeader(AUTHORIZATION, required = false, defaultValue = "")
        authorization: String,
    ): GetServiceInquiriesResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = GetServiceInquiriesCommand(page = page, count = count, requesterId = requesterId)
        return serviceInquiryService.get(command).toGetServiceInquiriesResponse()
    }

    @Transactional
    @PatchMapping("/{serviceInquiryId}")
    suspend fun modifyServiceInquiry(
        @PathVariable("serviceInquiryId", required = false)
        serviceInquiryId: String,
        @RequestBody
        request: ModifyServiceInquiryRequest,
        @RequestHeader(AUTHORIZATION, required = false, defaultValue = "")
        authorization: String,
    ): ModifyServiceInquiryResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyServiceInquiryCommand = request.toCommand(serviceInquiryId, requesterId)
        val serviceInquiry: ServiceInquiry = serviceInquiryService.modify(command)
        val serviceInquiryImageList: List<ServiceInquiryImage> = serviceInquiryImageService.modify(command, serviceInquiry)
        return ModifyServiceInquiryResponse(ServiceInquiryResponse(serviceInquiry, serviceInquiryImageList))
    }

}