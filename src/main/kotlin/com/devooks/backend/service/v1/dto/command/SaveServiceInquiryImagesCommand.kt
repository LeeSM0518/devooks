package com.devooks.backend.service.v1.dto.command

import com.devooks.backend.common.domain.Image
import java.util.*

class SaveServiceInquiryImagesCommand(
    val imageList: List<Image>,
    val requesterId: UUID,
)
