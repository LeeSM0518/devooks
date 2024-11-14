package com.devooks.backend.service.v1.repository

import com.devooks.backend.service.v1.entity.ServiceInquiryEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceInquiryCrudRepository : CoroutineCrudRepository<ServiceInquiryEntity, UUID>
