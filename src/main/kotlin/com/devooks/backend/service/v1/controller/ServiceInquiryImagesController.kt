package com.devooks.backend.service.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.service.v1.controller.docs.ServiceInquiryImagesControllerDocs
import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import com.devooks.backend.service.v1.dto.command.SaveServiceInquiryImagesCommand
import com.devooks.backend.service.v1.dto.request.SaveServiceInquiryImagesRequest
import com.devooks.backend.service.v1.dto.response.SaveServiceInquiryImagesResponse
import com.devooks.backend.service.v1.dto.response.SaveServiceInquiryImagesResponse.Companion.toSaveServiceInquiryImagesResponse
import com.devooks.backend.service.v1.service.ServiceInquiryImageService
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/service-inquiries/images")
class ServiceInquiryImagesController(
    private val serviceInquiryImageService: ServiceInquiryImageService,
    private val tokenService: TokenService,
): ServiceInquiryImagesControllerDocs {

    @Transactional
    @PostMapping
    override suspend fun saveServiceInquiryImages(
        @RequestBody
        request: SaveServiceInquiryImagesRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): SaveServiceInquiryImagesResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: SaveServiceInquiryImagesCommand = request.toCommand(requesterId)
        val imageList: List<ServiceInquiryImage> = serviceInquiryImageService.save(command.imageList, requesterId)
        return imageList.toSaveServiceInquiryImagesResponse()
    }

}
