package com.devooks.backend.category.v1.repository

import com.devooks.backend.category.v1.entity.CategoryEntity
import java.util.*
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CoroutineCrudRepository<CategoryEntity, UUID> {

    suspend fun findByNameIsIgnoreCase(name: String): CategoryEntity?
    suspend fun findAllByNameLikeIgnoreCase(name: String): Flow<CategoryEntity>
    suspend fun findAllByNameLikeIgnoreCase(name: String, pageable: Pageable): Flow<CategoryEntity>
}
