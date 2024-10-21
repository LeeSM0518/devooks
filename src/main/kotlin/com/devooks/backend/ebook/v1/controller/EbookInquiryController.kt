package com.devooks.backend.ebook.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.ebook.v1.domain.EbookInquiry
import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommand
import com.devooks.backend.ebook.v1.dto.command.DeleteEbookInquiryCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookInquiresCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommand
import com.devooks.backend.ebook.v1.dto.request.CreateEbookInquiryRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookInquiryRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryResponse.Companion.toCreateEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.DeleteEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.GetEbookInquiriesResponse
import com.devooks.backend.ebook.v1.dto.response.GetEbookInquiriesResponse.Companion.toGetEbookInquiriesResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryResponse.Companion.toModifyEbookInquiryResponse
import com.devooks.backend.ebook.v1.service.EbookInquiryEventService
import com.devooks.backend.ebook.v1.service.EbookInquiryService
import com.devooks.backend.ebook.v1.service.EbookService
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
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
@RequestMapping("/api/v1/ebook-inquiries")
class EbookInquiryController(
    private val ebookService: EbookService,
    private val tokenService: TokenService,
    private val ebookInquiryService: EbookInquiryService,
    private val ebookInquiryEventService: EbookInquiryEventService,
) {

    @Transactional
    @PostMapping
    suspend fun createEbookInquiry(
        @RequestBody
        request: CreateEbookInquiryRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): CreateEbookInquiryResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: CreateEbookInquiryCommand = request.toCommand(requesterId)
        ebookService.validate(command)
        val inquiry: EbookInquiry = ebookInquiryService.create(command)
        ebookInquiryEventService.publish(inquiry)
        return inquiry.toCreateEbookInquiryResponse()
    }

    @GetMapping
    suspend fun getEbookInquiries(
        @RequestParam(required = false, defaultValue = "")
        ebookId: String,
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
    ): GetEbookInquiriesResponse {
        val command = GetEbookInquiresCommand(ebookId, page, count)
        val ebookInquiryList: List<EbookInquiry> = ebookInquiryService.get(command)
        return ebookInquiryList.toGetEbookInquiriesResponse()
    }

    @Transactional
    @PatchMapping("/{inquiryId}")
    suspend fun modifyEbookInquiry(
        @PathVariable(name = "inquiryId", required = false)
        inquiryId: String,
        @RequestBody
        request: ModifyEbookInquiryRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyEbookInquiryResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyEbookInquiryCommand = request.toCommand(inquiryId, requesterId)
        val ebookInquiry: EbookInquiry = ebookInquiryService.modify(command)
        return ebookInquiry.toModifyEbookInquiryResponse()
    }

    @Transactional
    @DeleteMapping("/{inquiryId}")
    suspend fun deleteEbookInquiry(
        @PathVariable(name = "inquiryId", required = false)
        inquiryId: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): DeleteEbookInquiryResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = DeleteEbookInquiryCommand(inquiryId, requesterId)
        ebookInquiryService.delete(command)
        return DeleteEbookInquiryResponse()
    }

}
