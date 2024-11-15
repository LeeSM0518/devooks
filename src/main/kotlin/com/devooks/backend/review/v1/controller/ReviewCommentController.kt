package com.devooks.backend.review.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.review.v1.domain.ReviewComment
import com.devooks.backend.review.v1.dto.CreateReviewCommentCommand
import com.devooks.backend.review.v1.dto.CreateReviewCommentRequest
import com.devooks.backend.review.v1.dto.CreateReviewCommentResponse
import com.devooks.backend.review.v1.dto.CreateReviewCommentResponse.Companion.toCreateReviewCommentResponse
import com.devooks.backend.review.v1.dto.DeleteReviewCommentCommand
import com.devooks.backend.review.v1.dto.DeleteReviewCommentResponse
import com.devooks.backend.review.v1.dto.GetReviewCommentsCommand
import com.devooks.backend.review.v1.dto.GetReviewCommentsResponse
import com.devooks.backend.review.v1.dto.GetReviewCommentsResponse.Companion.toGetReviewCommentsResponse
import com.devooks.backend.review.v1.dto.ModifyReviewCommentCommand
import com.devooks.backend.review.v1.dto.ModifyReviewCommentRequest
import com.devooks.backend.review.v1.dto.ModifyReviewCommentResponse
import com.devooks.backend.review.v1.dto.ModifyReviewCommentResponse.Companion.toModifyReviewCommentResponse
import com.devooks.backend.review.v1.service.ReviewCommentEventService
import com.devooks.backend.review.v1.service.ReviewCommentService
import com.devooks.backend.review.v1.service.ReviewService
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
@RequestMapping("/api/v1/review-comments")
class ReviewCommentController(
    private val reviewCommentService: ReviewCommentService,
    private val reviewCommentEventService: ReviewCommentEventService,
    private val tokenService: TokenService,
    private val reviewService: ReviewService,
) {

    @Transactional
    @PostMapping
    suspend fun createReviewComment(
        @RequestBody
        request: CreateReviewCommentRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): CreateReviewCommentResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: CreateReviewCommentCommand = request.toCommand(requesterId)
        reviewService.validate(command)
        val reviewComment: ReviewComment = reviewCommentService.create(command)
        reviewCommentEventService.publish(reviewComment)
        return reviewComment.toCreateReviewCommentResponse()
    }

    @GetMapping
    suspend fun getReviewComments(
        @RequestParam(required = false, defaultValue = "")
        reviewId: String,
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
    ): GetReviewCommentsResponse {
        val command = GetReviewCommentsCommand(reviewId, page, count)
        val reviewCommentList: List<ReviewComment> = reviewCommentService.get(command)
        return reviewCommentList.toGetReviewCommentsResponse()
    }

    @Transactional
    @PatchMapping("/{commentId}")
    suspend fun modifyReviewComment(
        @PathVariable(name = "commentId", required = false)
        commentId: String,
        @RequestBody
        request: ModifyReviewCommentRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyReviewCommentResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyReviewCommentCommand = request.toCommand(commentId, requesterId)
        val reviewComment: ReviewComment = reviewCommentService.modify(command)
        return reviewComment.toModifyReviewCommentResponse()
    }

    @Transactional
    @DeleteMapping("/{commentId}")
    suspend fun deleteReviewComment(
        @PathVariable(name = "commentId", required = false)
        commentId: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): DeleteReviewCommentResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = DeleteReviewCommentCommand(commentId, requesterId)
        reviewCommentService.delete(command)
        return DeleteReviewCommentResponse()
    }

}
