package com.devooks.backend.ebook.v1.repository

import com.devooks.backend.ebook.v1.entity.RelatedCategoryEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RelatedCategoryRepository : CoroutineCrudRepository<RelatedCategoryEntity, UUID> {
    suspend fun findAllByEbookId(ebookId: UUID): List<RelatedCategoryEntity>
    suspend fun deleteAllByEbookId(ebookId: UUID)
}
