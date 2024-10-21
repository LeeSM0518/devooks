package com.devooks.backend.service.v1.domain

import java.nio.file.Path
import java.util.*

class ServiceInquiryImage(
    val id: UUID,
    val imagePath: Path,
    val order: Int,
    val uploadMemberId: UUID,
    val serviceInquiryId: UUID?
)
