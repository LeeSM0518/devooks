package com.devooks.backend.common.error

import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.dto.ImageDto
import com.devooks.backend.common.exception.GeneralException
import java.util.*

inline fun <reified T> T?.validateNotNull(exception: GeneralException): T =
    takeIf { it != null } ?: throw exception

fun String?.validateNotBlank(exception: GeneralException): String =
    takeIf { it.isNullOrBlank().not() } ?: throw exception

fun <T : Any> List<T>?.validateNotEmpty(exception: GeneralException): List<T> =
    takeIf { !isNullOrEmpty() } ?: throw exception

fun ImageDto?.validateImage(): Image =
    this?.toDomain() ?: throw CommonError.REQUIRED_IMAGE.exception

fun List<ImageDto>?.validateImages(): List<Image> =
    takeIf { it.isNullOrEmpty().not() }?.map { it.toDomain() } ?: throw CommonError.REQUIRED_IMAGE.exception

fun Int?.validateImageOrder(): Int =
    takeIf { it != null && it >= 0 } ?: throw CommonError.INVALID_IMAGE_ORDER.exception

fun String?.validateUUID(exception: GeneralException): UUID =
    runCatching { UUID.fromString(this) }.getOrElse { throw exception }