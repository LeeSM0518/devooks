package com.devooks.backend.service.v1.entity

import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import java.util.*
import kotlin.io.path.Path
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "service_inquiry_image")
data class ServiceInquiryImageEntity(
    @Id
    @Column("service_inquiry_image_id")
    @get:JvmName("serviceInquiryImageId")
    val id: UUID? = null,
    val imagePath: String,
    val imageOrder: Int,
    val uploadMemberId: UUID,
    val serviceInquiryId: UUID? = null,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        ServiceInquiryImage(
            id = id!!,
            imagePath = Path(imagePath),
            order = imageOrder,
            uploadMemberId = uploadMemberId,
            serviceInquiryId = serviceInquiryId
        )
}
