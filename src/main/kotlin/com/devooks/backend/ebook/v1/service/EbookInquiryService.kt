package com.devooks.backend.ebook.v1.service

import com.devooks.backend.ebook.v1.domain.EbookInquiry
import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommand
import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.dto.command.DeleteEbookInquiryCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookInquiresCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommand
import com.devooks.backend.ebook.v1.entity.EbookInquiryEntity
import com.devooks.backend.ebook.v1.error.EbookError
import com.devooks.backend.ebook.v1.repository.EbookInquiryRepository
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class EbookInquiryService(
    private val ebookInquiryRepository: EbookInquiryRepository,
) {
    suspend fun create(command: CreateEbookInquiryCommand): EbookInquiry {
        val entity = EbookInquiryEntity(
            ebookId = command.ebookId,
            content = command.content,
            writerMemberId = command.requesterId
        )
        return ebookInquiryRepository.save(entity).toDomain()
    }

    suspend fun get(command: GetEbookInquiresCommand): List<EbookInquiry> =
        ebookInquiryRepository
            .findAllByEbookId(command.ebookId, command.pageable)
            .map { it.toDomain() }
            .toList()

    suspend fun modify(command: ModifyEbookInquiryCommand): EbookInquiry =
        findById(command.inquiryId)
            .also { ebookInquiry -> validateRequesterId(ebookInquiry, command.requesterId) }
            .copy(content = command.content, modifiedDate = Instant.now())
            .let { ebookInquiryRepository.save(it) }
            .toDomain()

    suspend fun delete(command: DeleteEbookInquiryCommand) {
        findById(command.inquiryId)
            .also { inquiry -> validateRequesterId(inquiry, command.requesterId) }
            .also { inquiry -> ebookInquiryRepository.delete(inquiry) }
    }

    suspend fun validate(command: CreateEbookInquiryCommentCommand) {
        findById(command.inquiryId)
    }

    private fun validateRequesterId(ebookInquiryEntity: EbookInquiryEntity, requesterId: UUID) {
        ebookInquiryEntity
            .takeIf { it.writerMemberId == requesterId }
            ?: throw EbookError.FORBIDDEN_MODIFY_EBOOK_INQUIRY.exception
    }

    suspend fun findById(ebookInquiryId: UUID): EbookInquiryEntity =
        ebookInquiryRepository
            .findById(ebookInquiryId)
            ?: throw EbookError.NOT_FOUND_EBOOK_INQUIRY.exception
}
