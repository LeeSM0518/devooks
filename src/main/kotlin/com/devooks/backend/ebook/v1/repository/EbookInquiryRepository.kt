package com.devooks.backend.ebook.v1.repository

import com.devooks.backend.ebook.v1.entity.EbookInquiryEntity
import java.util.*
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EbookInquiryRepository : CoroutineCrudRepository<EbookInquiryEntity, UUID> {
    suspend fun findAllByEbookId(ebookId: UUID, pageable: Pageable): Flow<EbookInquiryEntity>
    suspend fun countByEbookId(ebookId: UUID): Long
}
