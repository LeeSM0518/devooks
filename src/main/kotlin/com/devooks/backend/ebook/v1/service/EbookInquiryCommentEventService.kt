package com.devooks.backend.ebook.v1.service

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import com.devooks.backend.member.v1.service.MemberService
import com.devooks.backend.notification.v1.domain.event.CreateEbookInquiryCommentEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EbookInquiryCommentEventService(
    private val memberService: MemberService,
    private val ebookInquiryService: EbookInquiryService,
    private val ebookService: EbookService,
    private val publisher: ApplicationEventPublisher,
) {

    suspend fun publish(ebookInquiryComment: EbookInquiryComment) {
        val member = memberService.findById(ebookInquiryComment.writerMemberId)
        val ebookInquiry = ebookInquiryService.findById(ebookInquiryComment.inquiryId)
        val ebook = ebookService.findById(ebookInquiry.ebookId)
        val createEbookInquiryCommentEvent = CreateEbookInquiryCommentEvent(
            ebookInquiryCommentId = ebookInquiryComment.id,
            ebookInquiryId = ebookInquiry.id!!,
            commenterName = member.nickname,
            ebookId = ebook.id,
            writtenDate = ebookInquiryComment.writtenDate,
            receiverId = ebookInquiry.writerMemberId
        )
        publisher.publishEvent(createEbookInquiryCommentEvent)
    }
}
