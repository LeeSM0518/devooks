package com.devooks.backend.notification.v1.adapter.`in`.event

import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.notification.v1.adapter.out.persistence.NotificationRepository
import com.devooks.backend.notification.v1.domain.event.CreateReviewEvent
import java.time.Instant
import java.util.*
import kotlin.test.AfterTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher

@IntegrationTest
internal class NotificationEventListenerTest @Autowired constructor(
    private val notificationRepository: NotificationRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @AfterTest
    fun tearDown() = runTest {
        notificationRepository.deleteAll()
    }

    @Test
    fun `도메인 생성 이벤트를 처리할 수 있다`(): Unit = runBlocking {
        // given
        val expected = CreateReviewEvent(
            receiverId = UUID.randomUUID(),
            reviewId = UUID.randomUUID(),
            reviewerName = "reviewerName",
            ebookId = UUID.randomUUID(),
            ebookTitle = "postTitle",
            writtenDate = Instant.now(),
        )

        // when
        applicationEventPublisher.publishEvent(expected)
        delay(100)

        // then
        val notifications = notificationRepository.findAll().toList()
        val actual = notifications[0]

        assertThat(notifications.size).isOne()
        assertThat(actual.content).isEqualTo(expected.content)
        assertThat(actual.note).isEqualTo(expected.createNote())
        assertThat(actual.receiverId).isEqualTo(expected.receiverId)
    }

}
