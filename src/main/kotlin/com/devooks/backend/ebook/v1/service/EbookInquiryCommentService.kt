package com.devooks.backend.ebook.v1.service

import com.devooks.backend.ebook.v1.domain.EbookInquiryComment
import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.dto.command.DeleteEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookInquireCommentsCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookInquiryCommentCommand
import com.devooks.backend.ebook.v1.entity.EbookInquiryCommentEntity
import com.devooks.backend.ebook.v1.error.EbookError
import com.devooks.backend.ebook.v1.repository.EbookInquiryCommentRepository
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class EbookInquiryCommentService(
    private val ebookInquiryCommentRepository: EbookInquiryCommentRepository,
) {
    suspend fun create(command: CreateEbookInquiryCommentCommand): EbookInquiryComment {
        val entity = EbookInquiryCommentEntity(
            inquiryId = command.inquiryId,
            content = command.content,
            writerMemberId = command.requesterId,
        )
        return ebookInquiryCommentRepository.save(entity).toDomain()
    }

    suspend fun get(command: GetEbookInquireCommentsCommand): List<EbookInquiryComment> =
        ebookInquiryCommentRepository
            .findAllByInquiryId(command.inquiryId, command.pageable)
            .map { it.toDomain() }
            .toList()

    suspend fun modify(command: ModifyEbookInquiryCommentCommand): EbookInquiryComment =
        findBy(command.commentId)
            .also { comment -> validateRequesterId(comment, command.requesterId) }
            .copy(content = command.content, modifiedDate = Instant.now())
            .let { ebookInquiryCommentRepository.save(it) }
            .toDomain()

    suspend fun delete(command: DeleteEbookInquiryCommentCommand) {
        findBy(command.commentId)
            .also { comment -> validateRequesterId(comment, command.requesterId) }
            .also { comment -> ebookInquiryCommentRepository.delete(comment) }
    }

    private fun validateRequesterId(comment: EbookInquiryCommentEntity, requesterId: UUID) {
        comment
            .takeIf { it.writerMemberId == requesterId }
            ?: throw EbookError.FORBIDDEN_MODIFY_EBOOK_INQUIRY_COMMENT.exception
    }

    private suspend fun findBy(commentId: UUID): EbookInquiryCommentEntity =
        ebookInquiryCommentRepository
            .findById(commentId)
            ?: throw EbookError.NOT_FOUND_EBOOK_INQUIRY_COMMENT.exception
}