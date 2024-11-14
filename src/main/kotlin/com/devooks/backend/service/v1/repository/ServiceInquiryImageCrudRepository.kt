package com.devooks.backend.service.v1.repository

import com.devooks.backend.service.v1.entity.ServiceInquiryImageEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceInquiryImageCrudRepository : CoroutineCrudRepository<ServiceInquiryImageEntity, UUID> {
    suspend fun findAllByServiceInquiryId(serviceInquiryId: UUID): List<ServiceInquiryImageEntity>
}
