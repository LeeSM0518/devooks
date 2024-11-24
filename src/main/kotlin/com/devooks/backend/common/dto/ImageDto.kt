package com.devooks.backend.common.dto

import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.domain.ImageExtension
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ImageDto(
    @field:NotBlank
    @Schema(description = "base64 프로필 사진", required = true)
    val base64Raw: String,
    @Schema(description = "확장자", required = true)
    val extension: ImageExtension,
    @field:Min(1)
    @field:Max(50_000_000)
    @Schema(description = "파일 크기 (byte, 최대 50MB)", required = true)
    val byteSize: Int,
) {
    fun toDomain(index: Int? = null): Image =
        Image(
            base64Raw = base64Raw,
            extension = extension,
            byteSize = byteSize,
            order = index ?: DEFAULT_IMAGE_ORDER
        )

    companion object {
        private const val DEFAULT_IMAGE_ORDER = 0

        fun List<ImageDto>.toDomain() =
            mapIndexed { index, imageDto -> imageDto.toDomain(index) }
    }
}
