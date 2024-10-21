package com.devooks.backend.ebook.v1.service

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import com.devooks.backend.member.v1.service.MemberService
import com.devooks.backend.notification.v1.domain.event.CreateEbookInquiryEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EbookInquiryEventService(
    private val memberService: MemberService,
    private val ebookService: EbookService,
    private val publisher: ApplicationEventPublisher,
) {
    suspend fun publish(ebookInquiry: EbookInquiry) {
        val member = memberService.findById(ebookInquiry.writerMemberId)
        val ebook = ebookService.findById(ebookInquiry.ebookId)
        val createEbookInquiryEvent = CreateEbookInquiryEvent(
            ebookInquiryId = ebookInquiry.id,
            inquirerName = member.nickname,
            ebookId = ebook.id,
            ebookTitle = ebook.title,
            writtenDate = ebookInquiry.writtenDate,
            receiverId = ebook.sellingMemberId
        )
        publisher.publishEvent(createEbookInquiryEvent)
    }
}
