package com.devooks.backend.review.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.ebook.v1.service.EbookService
import com.devooks.backend.review.v1.domain.Review
import com.devooks.backend.review.v1.dto.CreateReviewCommand
import com.devooks.backend.review.v1.dto.CreateReviewRequest
import com.devooks.backend.review.v1.dto.CreateReviewResponse
import com.devooks.backend.review.v1.dto.CreateReviewResponse.Companion.toCreateReviewResponse
import com.devooks.backend.review.v1.dto.DeleteReviewCommand
import com.devooks.backend.review.v1.dto.DeleteReviewResponse
import com.devooks.backend.review.v1.dto.GetReviewsCommand
import com.devooks.backend.review.v1.dto.GetReviewsResponse
import com.devooks.backend.review.v1.dto.GetReviewsResponse.Companion.toGetReviewsResponse
import com.devooks.backend.review.v1.dto.ModifyReviewCommand
import com.devooks.backend.review.v1.dto.ModifyReviewRequest
import com.devooks.backend.review.v1.dto.ModifyReviewResponse
import com.devooks.backend.review.v1.dto.ModifyReviewResponse.Companion.toModifyReviewResponse
import com.devooks.backend.review.v1.service.ReviewEventService
import com.devooks.backend.review.v1.service.ReviewService
import com.devooks.backend.transaciton.v1.service.TransactionService
import java.util.*
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
@RequestMapping("/api/v1/reviews")
class ReviewController(
    private val reviewService: ReviewService,
    private val tokenService: TokenService,
    private val transactionService: TransactionService,
    private val ebookService: EbookService,
    private val reviewEventService: ReviewEventService,
) {

    @Transactional
    @PostMapping
    suspend fun createReview(
        @RequestBody
        request: CreateReviewRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): CreateReviewResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: CreateReviewCommand = request.toCommand(requesterId)
        ebookService.validate(command)
        transactionService.validate(command)
        val review: Review = reviewService.create(command)
        reviewEventService.publish(review)
        return review.toCreateReviewResponse()
    }

    @GetMapping
    suspend fun getReviews(
        @RequestParam(required = false, defaultValue = "")
        ebookId: String,
        @RequestParam(required = false, defaultValue = "")
        memberId: String,
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
    ): GetReviewsResponse {
        val command = GetReviewsCommand(ebookId, memberId, page, count)
        val reviewList: List<Review> = reviewService.get(command)
        return reviewList.toGetReviewsResponse()
    }

    @Transactional
    @PatchMapping("/{reviewId}")
    suspend fun modifyReview(
        @PathVariable(name = "reviewId", required = false)
        reviewId: String,
        @RequestBody
        request: ModifyReviewRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyReviewResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyReviewCommand = request.toCommand(reviewId, requesterId)
        val review: Review = reviewService.modify(command)
        return review.toModifyReviewResponse()
    }

    @Transactional
    @DeleteMapping("/{reviewId}")
    suspend fun deleteReview(
        @PathVariable(name = "reviewId", required = false)
        reviewId: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): DeleteReviewResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command = DeleteReviewCommand(reviewId, requesterId)
        reviewService.delete(command)
        return DeleteReviewResponse()
    }

}
