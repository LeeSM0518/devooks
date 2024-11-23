package com.devooks.backend.service.v1.repository

import com.devooks.backend.common.config.database.JooqR2dbcRepository
import com.devooks.backend.jooq.tables.references.SERVICE_INQUIRY
import com.devooks.backend.jooq.tables.references.SERVICE_INQUIRY_IMAGE
import com.devooks.backend.service.v1.dto.command.GetServiceInquiriesCommand
import com.devooks.backend.service.v1.repository.row.ServiceInquiryRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jooq.impl.DSL
import org.jooq.impl.DSL.key
import org.springframework.stereotype.Repository

@Repository
class ServiceInquiryQueryRepository : JooqR2dbcRepository() {

    suspend fun findBy(command: GetServiceInquiriesCommand): Flow<ServiceInquiryRow> =
        query {
            val inquiry = SERVICE_INQUIRY
            val image = SERVICE_INQUIRY_IMAGE
            select(
                inquiry.SERVICE_INQUIRY_ID.`as`("id"),
                inquiry.TITLE,
                inquiry.CONTENT,
                inquiry.CREATED_DATE,
                inquiry.MODIFIED_DATE,
                inquiry.INQUIRY_PROCESSING_STATUS,
                inquiry.WRITER_MEMBER_ID,
                DSL.coalesce(
                    DSL.jsonArrayAgg(
                        DSL.jsonObject(
                            key("id").value(image.SERVICE_INQUIRY_IMAGE_ID),
                            key("image_path").value(image.IMAGE_PATH),
                            key("order").value(image.IMAGE_ORDER),
                        )
                    ),
                    DSL.inline("[]")
                ).`as`("image_json_data")
            ).from(
                inquiry.join(image).on(image.SERVICE_INQUIRY_ID.eq(inquiry.SERVICE_INQUIRY_ID))
            ).where(
                inquiry.WRITER_MEMBER_ID.eq(command.requesterId)
            ).groupBy(
                inquiry.SERVICE_INQUIRY_ID.`as`("id"),
                inquiry.TITLE,
                inquiry.CONTENT,
                inquiry.CREATED_DATE,
                inquiry.MODIFIED_DATE,
                inquiry.INQUIRY_PROCESSING_STATUS,
                inquiry.WRITER_MEMBER_ID,
            ).orderBy(inquiry.CREATED_DATE.desc())
                .offset(command.offset).limit(command.limit)
        }.map {
            it.into(ServiceInquiryRow::class.java)
        }

    suspend fun countBy(command: GetServiceInquiriesCommand): Flow<Long> =
        query {
            val inquiry = SERVICE_INQUIRY

            select(
                DSL.count()
            ).from(
                inquiry
            ).where(
                inquiry.WRITER_MEMBER_ID.eq(command.requesterId)
            )
        }.map { it.into(Long::class.java) }

}
