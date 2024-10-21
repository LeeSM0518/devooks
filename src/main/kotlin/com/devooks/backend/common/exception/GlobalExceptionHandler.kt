package com.devooks.backend.common.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.MissingRequestValueException
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalErrorWebExceptionHandler(
    globalErrorAttributes: DefaultErrorAttributes,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer,
    val objectMapper: ObjectMapper
) : AbstractErrorWebExceptionHandler(globalErrorAttributes, WebProperties.Resources(), applicationContext) {

    init {
        super.setMessageReaders(serverCodecConfigurer.readers)
        super.setMessageWriters(serverCodecConfigurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse)
    }

    private fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults())

        return when (val error = getError(request)) {
            is WebExchangeBindException -> {
                errorAttributes["errors"] = error.bindingResult.allErrors.map { "${it.code} : ${it.defaultMessage}" }
                ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorAttributes))
            }

            is GeneralException -> {
                errorAttributes["code"] = error.code
                errorAttributes["message"] =
                    if (error.code == "MEMBER-404-1") {
                        objectMapper.readValue<Map<String, String>>(error.message)
                    } else {
                        error.message
                    }
                errorAttributes["status"] = error.status.value()
                errorAttributes["error"] = error.status.name
                ServerResponse
                    .status(error.status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorAttributes))
            }

            is MissingRequestValueException -> {
                errorAttributes["reason"] = error.reason
                ServerResponse
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorAttributes))
            }

            is ServerWebInputException -> {
                errorAttributes["reason"] = error.cause.toString()
                ServerResponse
                    .status(error.statusCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorAttributes))
            }

            is NoResourceFoundException -> {
                errorAttributes["reason"] = "존재하지 않는 API 입니다."
                ServerResponse
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorAttributes))
            }

            else -> {
                ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(errorAttributes))
            }
        }
    }
}
