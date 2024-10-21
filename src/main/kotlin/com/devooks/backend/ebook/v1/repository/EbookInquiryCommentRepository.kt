package com.devooks.backend.ebook.v1.repository

import com.devooks.backend.ebook.v1.entity.EbookInquiryCommentEntity
import java.util.*
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EbookInquiryCommentRepository : CoroutineCrudRepository<EbookInquiryCommentEntity, UUID> {
    suspend fun findAllByInquiryId(inquiryId: UUID, pageable: Pageable): Flow<EbookInquiryCommentEntity>
}
