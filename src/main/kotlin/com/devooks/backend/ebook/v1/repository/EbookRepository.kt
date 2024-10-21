package com.devooks.backend.ebook.v1.repository

import com.devooks.backend.ebook.v1.entity.EbookEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EbookRepository : CoroutineCrudRepository<EbookEntity, UUID>
