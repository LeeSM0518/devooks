package com.devooks.backend.service.v1.repository

import com.devooks.backend.service.v1.domain.InquiryProcessingStatus.Companion.toInquiryProcessingStatus
import com.devooks.backend.service.v1.dto.ServiceInquiryImageDto
import com.devooks.backend.service.v1.dto.ServiceInquiryView
import com.devooks.backend.service.v1.dto.command.GetServiceInquiriesCommand
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.r2dbc.spi.Readable
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class ServiceInquiryQueryRepository(
    private val databaseClient: DatabaseClient,
) {
    private val objectMapper: ObjectMapper = ObjectMapper()

    suspend fun findBy(command: GetServiceInquiriesCommand): List<ServiceInquiryView> {
        val binding = mutableMapOf<String, Any>()
        binding["memberId"] = command.requesterId
        val query = """
            SELECT service_inquiry_id                                   AS id,
                   title,
                   content,
                   created_date,
                   modified_date,
                   inquiry_processing_status,
                   writer_member_id,
                   (SELECT ARRAY_TO_JSON(ARRAY_AGG(ROW_TO_JSON(si.*)))
                    FROM service_inquiry_image si
                    WHERE s.service_inquiry_id = si.service_inquiry_id) AS image_list
            FROM service_inquiry s
            WHERE writer_member_id = :memberId
            ORDER BY s.created_date DESC
            OFFSET ${command.offset} LIMIT ${command.limit}
        """.trimIndent()

        return databaseClient
            .sql(query)
            .bindValues(binding)
            .map { row -> mapToServiceInquiryView(row) }
            .all()
            .asFlow()
            .toList()
    }

    private fun mapToServiceInquiryView(row: Readable) =
        ServiceInquiryView(
            id = row.get("id", UUID::class.java)!!,
            title = row.get("title", String::class.java)!!,
            content = row.get("content", String::class.java)!!,
            createdDate = row.get("created_date", Instant::class.java)!!,
            modifiedDate = row.get("modified_date", Instant::class.java)!!,
            inquiryProcessingStatus = row.get("inquiry_processing_status", String::class.java)!!
                .toInquiryProcessingStatus(),
            writerMemberId = row.get("writer_member_id", UUID::class.java)!!,
            imageList = row.get("image_list", String::class.java)?.let {
                val imageList = objectMapper.readValue<List<Map<String, String>>>(it)
                imageList.map { image ->
                    ServiceInquiryImageDto(
                        id = UUID.fromString(image["service_inquiry_image_id"]!!),
                        imagePath = image["image_path"]!!,
                        order = image["image_order"]!!.toInt()
                    )
                }
            } ?: listOf()
        )


}
