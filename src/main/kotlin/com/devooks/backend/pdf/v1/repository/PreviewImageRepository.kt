package com.devooks.backend.pdf.v1.repository

import com.devooks.backend.pdf.v1.entity.PreviewImageEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PreviewImageRepository : CoroutineCrudRepository<PreviewImageEntity, UUID> {

    suspend fun findAllByPdfId(pdfId: UUID): List<PreviewImageEntity>
}
