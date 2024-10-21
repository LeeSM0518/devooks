package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.domain.Image
import java.util.*

class SaveMainImageCommand(
    val image: Image,
    val requesterId: UUID,
)

