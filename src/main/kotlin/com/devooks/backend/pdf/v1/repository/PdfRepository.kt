package com.devooks.backend.pdf.v1.repository

import com.devooks.backend.pdf.v1.entity.PdfEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PdfRepository : CoroutineCrudRepository<PdfEntity, UUID>
