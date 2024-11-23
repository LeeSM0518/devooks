package com.devooks.backend.ebook.v1.service

import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.dto.command.CreateEbookCommand
import com.devooks.backend.ebook.v1.dto.command.CreateEbookInquiryCommand
import com.devooks.backend.ebook.v1.dto.command.DeleteEbookCommand
import com.devooks.backend.ebook.v1.dto.command.GetDetailOfEbookCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import com.devooks.backend.ebook.v1.entity.EbookEntity
import com.devooks.backend.ebook.v1.entity.EbookEntity.Companion.toEntity
import com.devooks.backend.ebook.v1.error.EbookError
import com.devooks.backend.ebook.v1.repository.EbookQueryRepository
import com.devooks.backend.ebook.v1.repository.EbookRepository
import com.devooks.backend.ebook.v1.repository.row.EbookDetailRow
import com.devooks.backend.ebook.v1.repository.row.EbookRow
import com.devooks.backend.review.v1.dto.CreateReviewCommand
import com.devooks.backend.transaciton.v1.dto.CreateTransactionCommand
import java.time.Instant.now
import java.util.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class EbookService(
    private val ebookRepository: EbookRepository,
    private val ebookQueryRepository: EbookQueryRepository,
) {

    suspend fun create(command: CreateEbookCommand): Ebook {
        val ebookEntity = ebookRepository.save(
            EbookEntity(
                sellingMemberId = command.sellingMemberId,
                pdfId = command.pdfId,
                mainImageId = command.mainImageId,
                title = command.title,
                price = command.price,
                tableOfContents = command.tableOfContents,
                introduction = command.introduction
            )
        )
        return ebookEntity.toDomain()
    }

    suspend fun findById(ebookId: UUID): Ebook =
        ebookRepository
            .findById(ebookId)
            ?.also {
                if (it.deletedDate != null) {
                    throw EbookError.NOT_FOUND_EBOOK.exception
                }
            }
            ?.toDomain()
            ?: throw EbookError.NOT_FOUND_EBOOK.exception

    suspend fun validate(command: CreateTransactionCommand) {
        findById(command.ebookId)
            .also {
                if (it.price != command.price) {
                    throw EbookError.INVALID_EBOOK_PRICE.exception
                }
            }
            .also { if (it.sellingMemberId == command.requesterId) throw EbookError.FORBIDDEN_BUYER_MEMBER_ID.exception }
    }

    suspend fun validate(command: CreateReviewCommand) {
        findById(command.ebookId)
    }

    suspend fun validate(command: CreateEbookInquiryCommand) {
        findById(command.ebookId)
    }

    suspend fun get(command: GetEbookCommand): Page<EbookRow> {
        val ebooks = ebookQueryRepository.findBy(command)
        val count = ebookQueryRepository.count(command)
        return PageImpl(ebooks.toList(), command.pageable, count.first())
    }

    suspend fun get(command: GetDetailOfEbookCommand): EbookDetailRow =
        ebookQueryRepository.findBy(command)
            ?: throw EbookError.NOT_FOUND_EBOOK.exception

    suspend fun modify(command: ModifyEbookCommand): Ebook {
        val ebook = findById(command.ebookId)
        return ebook
            .takeIf { command.isChangedEbook }
            ?.validateToModify(command)
            ?.modify(command)
            ?.let { ebookRepository.save(it.toEntity()).toDomain() }
            ?: ebook
    }

    suspend fun delete(command: DeleteEbookCommand) {
        findById(command.ebookId)
            .validateToDelete(command)
            .also { ebookRepository.save(it.copy(deletedDate = now()).toEntity()) }
    }

    private suspend fun Ebook.validateToModify(command: ModifyEbookCommand): Ebook =
        takeIf { it.sellingMemberId == command.requesterId }
            ?: throw EbookError.FORBIDDEN_MODIFY_EBOOK.exception

    private suspend fun Ebook.validateToDelete(command: DeleteEbookCommand): Ebook =
        takeIf { it.sellingMemberId == command.requesterId }
            ?: throw EbookError.FORBIDDEN_DELETE_EBOOK.exception

}
