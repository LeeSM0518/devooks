package com.devooks.backend.service.v1.service

import com.devooks.backend.service.v1.domain.ServiceInquiry
import com.devooks.backend.service.v1.dto.ServiceInquiryView
import com.devooks.backend.service.v1.dto.command.CreateServiceInquiryCommand
import com.devooks.backend.service.v1.dto.command.GetServiceInquiriesCommand
import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import com.devooks.backend.service.v1.entity.ServiceInquiryEntity
import com.devooks.backend.service.v1.entity.ServiceInquiryEntity.Companion.toEntity
import com.devooks.backend.service.v1.error.ServiceInquiryError
import com.devooks.backend.service.v1.repository.ServiceInquiryQueryRepository
import com.devooks.backend.service.v1.repository.ServiceInquiryRepository
import java.util.*
import org.springframework.stereotype.Service

@Service
class ServiceInquiryService(
    private val serviceInquiryRepository: ServiceInquiryRepository,
    private val serviceInquiryQueryRepository: ServiceInquiryQueryRepository,
) {
    suspend fun create(command: CreateServiceInquiryCommand): ServiceInquiry {
        val serviceInquiryEntity = serviceInquiryRepository.save(
            ServiceInquiryEntity(
                title = command.title,
                content = command.content,
                writerMemberId = command.requesterId
            )
        )
        return serviceInquiryEntity.toDomain()
    }

    suspend fun get(command: GetServiceInquiriesCommand): List<ServiceInquiryView> =
        serviceInquiryQueryRepository.findBy(command)

    suspend fun modify(command: ModifyServiceInquiryCommand): ServiceInquiry {
        val serviceInquiry = findBy(command.serviceInquiryId)
        return findBy(command.serviceInquiryId)
            .takeIf { command.isChangedServiceInquiry }
            ?.validate(command)
            ?.modify(command)
            ?.let { serviceInquiryRepository.save(it.toEntity()).toDomain() }
            ?: serviceInquiry
    }

    private suspend fun findBy(id: UUID) =
        serviceInquiryRepository
            .findById(id)
            ?.toDomain()
            ?: throw ServiceInquiryError.NOT_FOUND_SERVICE_INQUIRY.exception

    private suspend fun ServiceInquiry.validate(command: ModifyServiceInquiryCommand) =
        takeIf { it.writerMemberId == command.requesterId }
            ?: throw ServiceInquiryError.FORBIDDEN_MODIFY_SERVICE_INQUIRY.exception

}
