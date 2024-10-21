package com.devooks.backend.ebook.v1.repository

import com.devooks.backend.ebook.v1.entity.EbookImageEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EbookImageRepository : CoroutineCrudRepository<EbookImageEntity, UUID> {
    suspend fun deleteAllByEbookId(ebookId: UUID)
    suspend fun findAllByEbookId(ebookId: UUID): List<EbookImageEntity>
}
