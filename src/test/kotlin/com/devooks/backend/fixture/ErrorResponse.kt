package com.devooks.backend.fixture

import com.devooks.backend.common.exception.GeneralException
import java.time.Instant
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.RequestBodyUriSpec
import org.springframework.test.web.reactive.server.expectBody

internal data class ErrorResponse(
    val timestamp: Instant,
    val path: String,
    val status: Int,
    val error: String,
    val requestId: String,
    val code: String,
    val message: String,
) {
    internal fun isEqualTo(exception: GeneralException) {
        assertThat(status).isEqualTo(exception.status.value())
        assertThat(error).isEqualTo(exception.status.name)
        assertThat(code).isEqualTo(exception.code)
        assertThat(message).isEqualTo(exception.message)
    }

    companion object {
        fun WebTestClient.postForBadRequest(
            uri: String,
            request: String,
            token: String? = null,
        ): ErrorResponse = post().getResponseBodyAboutBadRequest(uri, request, token)

        fun WebTestClient.patchForBadRequest(
            uri: String,
            request: String,
            token: String? = null,
        ): ErrorResponse = patch().getResponseBodyAboutBadRequest(uri, request, token)

        fun WebTestClient.patchForConflict(
            uri: String,
            request: String,
            token: String? = null,
        ): ErrorResponse = patch().getResponseBodyAboutConflict(uri, request, token)

        private fun RequestBodyUriSpec.getResponseBodyAboutBadRequest(
            uri: String,
            request: String,
            token: String? = null,
        ): ErrorResponse =
            this.uri(uri)
                .apply { token?.also { header(AUTHORIZATION, it) } }
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ErrorResponse>()
                .returnResult()
                .responseBody!!

        private fun RequestBodyUriSpec.getResponseBodyAboutConflict(
            uri: String,
            request: String,
            token: String? = null,
        ): ErrorResponse =
            this.uri(uri)
                .apply { token?.also { header(AUTHORIZATION, it) } }
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody<ErrorResponse>()
                .returnResult()
                .responseBody!!
    }
}