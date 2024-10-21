package com.devooks.backend.common.exception

import org.springframework.http.HttpStatus

data class GeneralException(
    val code: String,
    val status: HttpStatus,
    override val message: String,
) : RuntimeException(message)