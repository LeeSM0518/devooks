package com.devooks.backend.service.v1.service

import com.devooks.backend.service.v1.domain.ServiceInquiry
import com.devooks.backend.service.v1.dto.command.CreateServiceInquiryCommand
import com.devooks.backend.service.v1.dto.command.GetServiceInquiriesCommand
import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import com.devooks.backend.service.v1.entity.ServiceInquiryEntity
import com.devooks.backend.service.v1.entity.ServiceInquiryEntity.Companion.toEntity
import com.devooks.backend.service.v1.error.ServiceInquiryError
import com.devooks.backend.service.v1.repository.ServiceInquiryCrudRepository
import com.devooks.backend.service.v1.repository.ServiceInquiryQueryRepository
import com.devooks.backend.service.v1.repository.row.ServiceInquiryRow
import java.util.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class ServiceInquiryService(
    private val serviceInquiryCrudRepository: ServiceInquiryCrudRepository,
    private val serviceInquiryQueryRepository: ServiceInquiryQueryRepository,
) {
    suspend fun create(command: CreateServiceInquiryCommand): ServiceInquiry {
        val serviceInquiryEntity = serviceInquiryCrudRepository.save(
            ServiceInquiryEntity(
                title = command.title,
                content = command.content,
                writerMemberId = command.requesterId
            )
        )
        return serviceInquiryEntity.toDomain()
    }

    suspend fun get(command: GetServiceInquiriesCommand): Page<ServiceInquiryRow> {
        val serviceInquiries = serviceInquiryQueryRepository.findBy(command)
        val count = serviceInquiryQueryRepository.countBy(command)
        return PageImpl(serviceInquiries.toList(), command.pageable, count.first())
    }

    suspend fun modify(command: ModifyServiceInquiryCommand): ServiceInquiry {
        val serviceInquiry = findBy(command.serviceInquiryId)
        return findBy(command.serviceInquiryId)
            .takeIf { command.isChangedServiceInquiry }
            ?.validate(command)
            ?.modify(command)
            ?.let { serviceInquiryCrudRepository.save(it.toEntity()).toDomain() }
            ?: serviceInquiry
    }

    private suspend fun findBy(id: UUID) =
        serviceInquiryCrudRepository
            .findById(id)
            ?.toDomain()
            ?: throw ServiceInquiryError.NOT_FOUND_SERVICE_INQUIRY.exception

    private suspend fun ServiceInquiry.validate(command: ModifyServiceInquiryCommand) =
        takeIf { it.writerMemberId == command.requesterId }
            ?: throw ServiceInquiryError.FORBIDDEN_MODIFY_SERVICE_INQUIRY.exception

}
