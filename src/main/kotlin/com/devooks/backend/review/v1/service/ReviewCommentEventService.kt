package com.devooks.backend.review.v1.service

import com.devooks.backend.ebook.v1.service.EbookService
import com.devooks.backend.member.v1.service.MemberService
import com.devooks.backend.notification.v1.domain.event.CreateReviewCommentEvent
import com.devooks.backend.review.v1.domain.ReviewComment
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ReviewCommentEventService(
    private val memberService: MemberService,
    private val reviewService: ReviewService,
    private val ebookService: EbookService,
    private val publisher: ApplicationEventPublisher,
) {

    suspend fun publish(reviewComment: ReviewComment) {
        val member = memberService.findById(reviewComment.writerMemberId)
        val review = reviewService.findById(reviewComment.reviewId)
        val ebook = ebookService.findById(review.ebookId)
        val createReviewCommentEvent = CreateReviewCommentEvent(
            reviewCommentId = reviewComment.id,
            reviewId = review.id!!,
            commenterName = member.nickname,
            ebookId = ebook.id,
            writtenDate = reviewComment.writtenDate,
            receiverId = review.writerMemberId
        )
        publisher.publishEvent(createReviewCommentEvent)
    }
}
