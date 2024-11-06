package com.devooks.backend.service.v1.repository

import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.MemberRepository
import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.dto.command.GetServiceInquiriesCommand
import com.devooks.backend.service.v1.entity.ServiceInquiryEntity
import com.devooks.backend.service.v1.entity.ServiceInquiryImageEntity
import java.time.Instant.now
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class ServiceInquiryQueryRepositoryTest @Autowired constructor(
    private val serviceInquiryQueryRepository: ServiceInquiryQueryRepository,
    private val serviceInquiryCrudRepository: ServiceInquiryCrudRepository,
    private val serviceInquiryCrudImageRepository: ServiceInquiryImageCrudRepository,
    private val memberRepository: MemberRepository,
) {

    lateinit var expectedInquiry: ServiceInquiryEntity
    lateinit var expectedInquiryImage: ServiceInquiryImageEntity
    lateinit var expectedMember: MemberEntity

    @BeforeTest
    fun setup() = runTest {
        val entity = MemberEntity(nickname = "name")
        expectedMember = memberRepository.save(entity)
        val inquiry = ServiceInquiryEntity(
            title = "title",
            content = "content",
            writerMemberId = expectedMember.id!!,
            inquiryProcessingStatus = InquiryProcessingStatus.WAITING,
            createdDate = now()
        )
        expectedInquiry = serviceInquiryCrudRepository.save(inquiry)
        val inquiryImage =
            ServiceInquiryImageEntity(
                imagePath = "test",
                imageOrder = 1,
                uploadMemberId = expectedMember.id!!,
                serviceInquiryId = expectedInquiry.id!!
            )
        expectedInquiryImage = serviceInquiryCrudImageRepository.save(inquiryImage)
    }

    @AfterTest
    fun tearDown() = runTest {
        serviceInquiryCrudImageRepository.deleteAll()
        serviceInquiryCrudRepository.deleteAll()
    }

    @Test
    fun `서비스 문의를 조회할 수 있다`() = runTest {
        // given
        val command = GetServiceInquiriesCommand("1", "10", expectedMember.id!!)

        // when
        val foundInquiry = serviceInquiryQueryRepository.findBy(command).toList()

        // then
        assertThat(foundInquiry.size).isEqualTo(1)
        assertThat(foundInquiry[0].id).isEqualTo(expectedInquiry.id)
        assertThat(foundInquiry[0].title).isEqualTo(expectedInquiry.title)
        assertThat(foundInquiry[0].content).isEqualTo(expectedInquiry.content)
        assertThat(foundInquiry[0].writerMemberId).isEqualTo(expectedMember.id)
        assertThat(foundInquiry[0].inquiryProcessingStatus).isEqualTo(expectedInquiry.inquiryProcessingStatus)
        val imageList = foundInquiry[0].imageList
        assertThat(imageList.size).isOne()
        assertThat(imageList[0].id).isEqualTo(expectedInquiryImage.id)
        assertThat(imageList[0].imagePath).isEqualTo(expectedInquiryImage.imagePath)
        assertThat(imageList[0].order).isEqualTo(expectedInquiryImage.imageOrder)
    }
}
