package com.devooks.backend.notification.v1.adapter.`in`.http

import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.config.IntegrationTest
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.repository.MemberRepository
import com.devooks.backend.notification.v1.adapter.`in`.dto.CheckNotificationResponse
import com.devooks.backend.notification.v1.adapter.`in`.dto.NotificationResponse
import com.devooks.backend.notification.v1.adapter.out.persistence.NotificationEntity
import com.devooks.backend.notification.v1.adapter.out.persistence.NotificationRepository
import com.devooks.backend.notification.v1.domain.Notification
import com.devooks.backend.notification.v1.domain.NotificationType
import java.time.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.http.codec.ServerSentEvent
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
internal class NotificationRouterTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val memberRepository: MemberRepository,
    private val notificationRepository: NotificationRepository,
    private val tokenService: TokenService,
) {
    lateinit var expectedMember: Member
    lateinit var expectedNotification: Notification

    @BeforeTest
    fun setup() = runTest {
        val memberEntity = MemberEntity(nickname = "nickname")
        expectedMember = memberRepository.save(memberEntity).toDomain()
        val notificationEntity = NotificationEntity(
            type = NotificationType.REVIEW,
            content = "content",
            note = mapOf("key" to "value"),
            receiverId = expectedMember.id,
            notifiedDate = Instant.now(),
            checked = false
        )
        expectedNotification = notificationRepository.save(notificationEntity).toDomain()
    }

    @AfterTest
    fun tearDown() = runTest {
        notificationRepository.deleteAll()
        memberRepository.deleteAll()
    }

    @Test
    fun `실시간으로 읽지 않은 알림의 개수를 조회할 수 있다`() = runTest {
        // given
        val accessToken = tokenService.createTokenGroup(expectedMember).accessToken

        // when
        val eventStream = webTestClient
            .get()
            .uri("/api/v1/notifications/count")
            .accept(TEXT_EVENT_STREAM)
            .header(AUTHORIZATION, accessToken)
            .exchange()
            .expectStatus().isOk
            .returnResult(ServerSentEvent::class.java)
            .responseBody
            .blockFirst()!!

        // then
        val streamCountResponse = eventStream.data() as LinkedHashMap<*, *>
        assertThat(streamCountResponse["countOfUncheckedNotification"]).isEqualTo(1)
    }

    @Test
    fun `알림 목록을 조회할 수 있다`() = runTest {
        // given
        val accessToken = tokenService.createTokenGroup(expectedMember).accessToken

        // when
        val notificationsResponse = webTestClient
            .get()
            .uri("/api/v1/notifications?page=1&size=10")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, accessToken)
            .exchange()
            .expectBody<PageResponse<NotificationResponse>>()
            .returnResult()
            .responseBody!!
            .data


        // then
        val actual = notificationsResponse[0]
        assertThat(actual.id).isEqualTo(expectedNotification.id)
        assertThat(actual.content).isEqualTo(expectedNotification.content)
        assertThat(actual.note).isEqualTo(expectedNotification.note)
        assertThat(actual.type).isEqualTo(expectedNotification.type)
        assertThat(actual.receiverId).isEqualTo(expectedNotification.receiverId)
        assertThat(actual.checked).isEqualTo(expectedNotification.checked)
        assertThat(actual.notifiedDate.toEpochMilli()).isEqualTo(expectedNotification.notifiedDate.toEpochMilli())
    }

    @Test
    fun `읽지 않은 모든 알림의 읽음 상태를 변경 요청할 수 있다`() = runTest {
        // given
        val accessToken = tokenService.createTokenGroup(expectedMember).accessToken

        // when
        val count = webTestClient
            .patch()
            .uri("/api/v1/notifications/checked")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, accessToken)
            .exchange()
            .expectStatus().isOk
            .expectBody<CheckNotificationResponse>()
            .returnResult()
            .responseBody!!
            .count

        // then
        assertThat(count).isOne()
    }

    @Test
    fun `읽지 않은 특정 알림의 읽음 상태를 변경 요청할 수 있다`() = runTest {
        // given
        val accessToken = tokenService.createTokenGroup(expectedMember).accessToken

        // when
        val count = webTestClient
            .patch()
            .uri("/api/v1/notifications/${expectedNotification.id}/checked")
            .accept(APPLICATION_JSON)
            .header(AUTHORIZATION, accessToken)
            .exchange()
            .expectStatus().isOk
            .expectBody<CheckNotificationResponse>()
            .returnResult()
            .responseBody!!
            .count

        // then
        assertThat(count).isOne()
    }
}
