package com.devooks.backend.common.exception

import com.devooks.backend.common.error.CommonError
import com.devooks.backend.common.exception.ErrorResponse.Companion.toErrorResponse
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
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.MissingRequestValueException
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalErrorWebExceptionHandler(
    globalErrorAttributes: DefaultErrorAttributes,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer,
    val objectMapper: ObjectMapper,
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
        val commonException = CommonError.INVALID_REQUEST.exception

        return when (val error = getError(request)) {
            is WebExchangeBindException -> {
                ServerResponse.status(BAD_REQUEST)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        errorAttributes
                            .toErrorResponse(
                                code = commonException.code,
                                error = BAD_REQUEST.name,
                                message = error
                                    .bindingResult
                                    .allErrors
                                    .map { "${it.codes?.first() ?: "request"} : ${it.defaultMessage}" }
                                    .toString()
                            )
                    )
            }

            is GeneralException -> {
                ServerResponse
                    .status(error.status)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        errorAttributes
                            .toErrorResponse(
                                code = error.code,
                                status = error.status.value(),
                                error = error.status.name,
                                message =
                                if (error.code == "MEMBER-404-1") {
                                    objectMapper.readValue<Map<String, String>>(error.message)
                                } else {
                                    error.message
                                }
                            )
                    )
            }

            is MissingRequestValueException -> {
                val exception = CommonError.INVALID_REQUEST.exception
                ServerResponse
                    .status(BAD_REQUEST)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        errorAttributes
                            .toErrorResponse(
                                code = exception.code,
                                error = BAD_REQUEST.name,
                                message = "${exception.message} ${error.reason ?: ""}"
                            )
                    )
            }

            is ServerWebInputException -> {
                val exception = CommonError.INVALID_REQUEST.exception
                ServerResponse
                    .status(BAD_REQUEST)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        errorAttributes
                            .toErrorResponse(
                                code = exception.code,
                                error = BAD_REQUEST.name,
                                message = "${exception.message} ${error.cause?.message ?: ""}"
                            )
                    )
            }

            is NoResourceFoundException -> {
                ServerResponse
                    .status(NOT_FOUND)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        errorAttributes
                            .toErrorResponse(
                                error = NOT_FOUND.name,
                                message = "존재하지 않는 API 입니다."
                            )
                    )
            }

            is MethodNotAllowedException -> {
                val exception = CommonError.INVALID_METHOD.exception
                ServerResponse
                    .status(error.statusCode)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        errorAttributes
                            .toErrorResponse(
                                error = METHOD_NOT_ALLOWED.name,
                                code = exception.code,
                                message = "${exception.message} ${error.cause?.message ?: ""}"
                            )
                    )
            }

            is UnsupportedMediaTypeStatusException -> {
                ServerResponse
                    .status(error.statusCode)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(
                        errorAttributes
                            .toErrorResponse(
                                error = HttpStatus.valueOf(error.statusCode.value()).name,
                                message = error.reason
                            )
                    )
            }

            else -> {
                ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(errorAttributes.toErrorResponse(message = error.cause.toString()))
            }
        }
    }
}
