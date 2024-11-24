package com.devooks.backend.service.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.dto.PageResponse.Companion.toResponse
import com.devooks.backend.service.v1.controller.docs.ServiceInquiryControllerDocs
import com.devooks.backend.service.v1.domain.ServiceInquiry
import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import com.devooks.backend.service.v1.dto.ServiceInquiryView
import com.devooks.backend.service.v1.dto.ServiceInquiryView.Companion.toServiceInquiryView
import com.devooks.backend.service.v1.dto.command.CreateServiceInquiryCommand
import com.devooks.backend.service.v1.dto.command.GetServiceInquiriesCommand
import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import com.devooks.backend.service.v1.dto.request.CreateServiceInquiryRequest
import com.devooks.backend.service.v1.dto.request.ModifyServiceInquiryRequest
import com.devooks.backend.service.v1.dto.response.CreateServiceInquiryResponse
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
) : ServiceInquiryControllerDocs {

    @Transactional
    @PostMapping
    override suspend fun createServiceInquiry(
        @RequestBody
        request: CreateServiceInquiryRequest,
        @RequestHeader(AUTHORIZATION)
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
    override suspend fun getServiceInquiries(
        @RequestParam
        page: Int,
        @RequestParam
        count: Int,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): PageResponse<ServiceInquiryView> {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = GetServiceInquiriesCommand(page = page, count = count, requesterId = requesterId)
        val pageServiceInquiry = serviceInquiryService.get(command)
        return pageServiceInquiry.map { it.toServiceInquiryView() }.toResponse()
    }

    @Transactional
    @PatchMapping("/{serviceInquiryId}")
    override suspend fun modifyServiceInquiry(
        @PathVariable("serviceInquiryId", required = true)
        serviceInquiryId: String,
        @RequestBody
        request: ModifyServiceInquiryRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyServiceInquiryResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyServiceInquiryCommand = request.toCommand(serviceInquiryId, requesterId)
        val serviceInquiry: ServiceInquiry = serviceInquiryService.modify(command)
        val serviceInquiryImageList: List<ServiceInquiryImage> =
            serviceInquiryImageService.modify(command, serviceInquiry)
        return ModifyServiceInquiryResponse(ServiceInquiryResponse(serviceInquiry, serviceInquiryImageList))
    }

}
