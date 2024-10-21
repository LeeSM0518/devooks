package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.domain.Image
import java.util.*

class SaveDescriptionImagesCommand(
    val imageList: List<Image>,
    val requesterId: UUID
)
