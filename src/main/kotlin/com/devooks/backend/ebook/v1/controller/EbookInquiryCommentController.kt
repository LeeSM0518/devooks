package com.devooks.backend.ebook.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.dto.PageResponse.Companion.toResponse
import com.devooks.backend.ebook.v1.controller.docs.EbookInquiryCommentControllerDocs
import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentView
import com.devooks.backend.ebook.v1.dto.EbookInquiryCommentView.Companion.toEbookInquiryCommentView
import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.dto.command.DeleteEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookInquireCommentsCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.dto.request.CreateEbookInquiryCommentRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookInquiryCommentRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.CreateEbookInquiryCommentResponse.Companion.toCreateEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.DeleteEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookInquiryCommentResponse.Companion.toModifyEbookInquiryCommentResponse
import com.devooks.backend.ebook.v1.service.EbookInquiryCommentEventService
import com.devooks.backend.ebook.v1.service.EbookInquiryCommentService
import com.devooks.backend.ebook.v1.service.EbookInquiryService
import org.springframework.data.domain.Page
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
@RequestMapping("/api/v1/ebook-inquiry-comments")
class EbookInquiryCommentController(
    private val ebookInquiryService: EbookInquiryService,
    private val tokenService: TokenService,
    private val ebookInquiryCommentService: EbookInquiryCommentService,
    private val ebookInquiryCommentEventService: EbookInquiryCommentEventService,
) : EbookInquiryCommentControllerDocs {

    @Transactional
    @PostMapping
    override suspend fun createEbookInquiryComment(
        @RequestBody
        request: CreateEbookInquiryCommentRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): CreateEbookInquiryCommentResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: CreateEbookInquiryCommentCommand = request.toCommand(requesterId)
        ebookInquiryService.validate(command)
        val inquiryComment: EbookInquiryComment = ebookInquiryCommentService.create(command)
        ebookInquiryCommentEventService.publish(inquiryComment)
        return inquiryComment.toCreateEbookInquiryCommentResponse()
    }

    @GetMapping
    override suspend fun getEbookInquiryComments(
        @RequestParam(required = true)
        inquiryId: String,
        @RequestParam(required = true)
        page: String,
        @RequestParam(required = true)
        count: String,
    ): PageResponse<EbookInquiryCommentView> {
        val command = GetEbookInquireCommentsCommand(inquiryId, page, count)
        val inquiryCommentList: Page<EbookInquiryComment> = ebookInquiryCommentService.get(command)
        return inquiryCommentList.map { it.toEbookInquiryCommentView() }.toResponse()
    }

    @Transactional
    @PatchMapping("/{commentId}")
    override suspend fun modifyEbookInquiryComment(
        @PathVariable(name = "commentId", required = true)
        commentId: String,
        @RequestBody
        request: ModifyEbookInquiryCommentRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyEbookInquiryCommentResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyEbookInquiryCommentCommand = request.toCommand(commentId, requesterId)
        val comment: EbookInquiryComment = ebookInquiryCommentService.modify(command)
        return comment.toModifyEbookInquiryCommentResponse()
    }

    @Transactional
    @DeleteMapping("/{commentId}")
    override suspend fun deleteEbookInquiryComment(
        @PathVariable(name = "commentId", required = true)
        commentId: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): DeleteEbookInquiryCommentResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = DeleteEbookInquiryCommentCommand(commentId, requesterId)
        ebookInquiryCommentService.delete(command)
        return DeleteEbookInquiryCommentResponse()
    }
}
