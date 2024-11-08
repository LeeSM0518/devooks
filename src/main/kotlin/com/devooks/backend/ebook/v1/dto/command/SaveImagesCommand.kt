package com.devooks.backend.ebook.v1.dto.command

import com.devooks.backend.common.domain.Image
import com.devooks.backend.ebook.v1.domain.EbookImageType
import java.util.*

class SaveImagesCommand(
    val imageList: List<Image>,
    val requesterId: UUID,
    val imageType: EbookImageType,
)
