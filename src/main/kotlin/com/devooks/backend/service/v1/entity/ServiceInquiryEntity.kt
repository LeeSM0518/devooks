package com.devooks.backend.service.v1.entity

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus
import com.devooks.backend.service.v1.domain.ServiceInquiry
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "service_inquiry")
data class ServiceInquiryEntity(
    @Id
    @Column(value = "service_inquiry_id")
    @get:JvmName("serviceInquiryId")
    val id: UUID? = null,
    val title: String,
    val content: String,
    val writerMemberId: UUID,
    val inquiryProcessingStatus: InquiryProcessingStatus = InquiryProcessingStatus.WAITING,
    val createdDate: Instant = Instant.now(),
    val modifiedDate: Instant = createdDate,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        ServiceInquiry(
            id = id!!,
            title = title,
            content = content,
            writerMemberId = writerMemberId,
            inquiryProcessingStatus = inquiryProcessingStatus,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
        )

    companion object {
        fun ServiceInquiry.toEntity() =
            ServiceInquiryEntity(
                id = id,
                title = title,
                content = content,
                writerMemberId = writerMemberId,
                inquiryProcessingStatus = inquiryProcessingStatus,
                createdDate = createdDate,
                modifiedDate = modifiedDate,
            )
    }
}
