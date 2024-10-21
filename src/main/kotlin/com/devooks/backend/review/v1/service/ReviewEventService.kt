package com.devooks.backend.review.v1.service

import com.devooks.backend.ebook.v1.service.EbookService
import com.devooks.backend.member.v1.service.MemberService
import com.devooks.backend.notification.v1.domain.event.CreateReviewEvent
import com.devooks.backend.review.v1.domain.Review
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ReviewEventService(
    private val memberService: MemberService,
    private val ebookService: EbookService,
    private val publisher: ApplicationEventPublisher,
) {

    suspend fun publish(review: Review) {
        val member = memberService.findById(review.writerMemberId)
        val ebook = ebookService.findById(review.ebookId)
        val createReviewEvent = CreateReviewEvent(
            reviewId = review.id,
            reviewerName = member.nickname,
            ebookId = ebook.id,
            ebookTitle = ebook.title,
            writtenDate = review.writtenDate,
            receiverId = ebook.sellingMemberId
        )
        publisher.publishEvent(createReviewEvent)
    }
}
