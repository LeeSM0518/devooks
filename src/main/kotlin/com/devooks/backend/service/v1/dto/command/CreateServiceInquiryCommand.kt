package com.devooks.backend.service.v1.dto.command

import java.util.*

class CreateServiceInquiryCommand(
    val title: String,
    val content: String,
    val imageIdList: List<UUID>?,
    val requesterId: UUID
)
