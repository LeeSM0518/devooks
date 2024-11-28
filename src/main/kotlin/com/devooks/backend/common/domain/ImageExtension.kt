package com.devooks.backend.common.domain

import java.util.*

enum class ImageExtension {
    JPG, PNG, JPEG;

    override fun toString(): String =
        this.name.lowercase(Locale.getDefault())
}
