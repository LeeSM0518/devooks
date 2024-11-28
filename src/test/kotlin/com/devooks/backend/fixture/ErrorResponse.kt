package com.devooks.backend.fixture

import com.devooks.backend.common.error.CommonError
import com.devooks.backend.common.exception.GeneralException
import java.time.Instant
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
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
    }

    companion object {
        fun RequestBodyUriSpec.isBadRequest(
            uri: String,
            request: Map<String, Any>,
            token: String? = null,
        ) {
            getResponseBodyAboutBadRequest(uri, request, token)
                .isEqualTo(CommonError.INVALID_REQUEST.exception)
        }

        private fun RequestBodyUriSpec.getResponseBodyAboutBadRequest(
            uri: String,
            request: Map<String, Any>,
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

    }
}
