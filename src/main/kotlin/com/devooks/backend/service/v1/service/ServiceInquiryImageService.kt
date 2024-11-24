package com.devooks.backend.service.v1.service

import com.devooks.backend.BackendApplication.Companion.SERVICE_INQUIRY_IMAGE_ROOT_PATH
import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.utils.saveImage
import com.devooks.backend.service.v1.domain.ServiceInquiry
import com.devooks.backend.service.v1.domain.ServiceInquiryImage
import com.devooks.backend.service.v1.dto.command.ModifyServiceInquiryCommand
import com.devooks.backend.service.v1.entity.ServiceInquiryImageEntity
import com.devooks.backend.service.v1.error.ServiceInquiryError
import com.devooks.backend.service.v1.repository.ServiceInquiryImageCrudRepository
import java.util.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class ServiceInquiryImageService(
    private val serviceInquiryImageCrudRepository: ServiceInquiryImageCrudRepository,
) {

    suspend fun save(
        imageList: List<Image>,
        requesterId: UUID,
    ): List<ServiceInquiryImage> =
        imageList
            .asFlow()
            .map {
                ServiceInquiryImageEntity(
                    imagePath = saveImage(it, SERVICE_INQUIRY_IMAGE_ROOT_PATH),
                    imageOrder = it.order,
                    uploadMemberId = requesterId
                )
            }
            .let { serviceInquiryImageCrudRepository.saveAll(it) }
            .map { it.toDomain() }
            .toList()

    suspend fun save(
        imageIdList: List<UUID>,
        serviceInquiry: ServiceInquiry,
    ): List<ServiceInquiryImage> =
        serviceInquiryImageCrudRepository
            .findAllById(imageIdList)
            .takeIf { imageList ->
                imageList.filter { image ->
                    image.uploadMemberId != serviceInquiry.writerMemberId
                }.count() == 0
            }
            ?.map { it.copy(serviceInquiryId = serviceInquiry.id) }
            ?.let { serviceInquiryImageCrudRepository.saveAll(it) }
            ?.map { it.toDomain() }
            ?.toList()
            ?: throw ServiceInquiryError.FORBIDDEN_REGISTER_SERVICE_INQUIRY_TO_IMAGE.exception

    suspend fun modify(
        command: ModifyServiceInquiryCommand,
        serviceInquiry: ServiceInquiry,
    ): List<ServiceInquiryImage> {
        val existingImageList = serviceInquiryImageCrudRepository
            .findAllByServiceInquiryId(command.serviceInquiryId)

        return command.imageIdList?.let { imageIdList ->
            val (imagesToDelete, imageToKeep) =
                partitionImages(existingImageList, imageIdList)
            val newImageIdList = imageIdList.filterNot { id -> imageToKeep.any { it.id == id } }
            save(newImageIdList, serviceInquiry)
            serviceInquiryImageCrudRepository.deleteAll(imagesToDelete)
            updateImageOrder(imageIdList)
        } ?: existingImageList
            .map { it.toDomain() }
            .sortedBy { it.order }
    }

    private suspend fun updateImageOrder(imageIdList: List<UUID>): List<ServiceInquiryImage> =
        serviceInquiryImageCrudRepository
            .findAllById(imageIdList)
            .map { it.copy(imageOrder = imageIdList.indexOf(it.id)) }
            .let { serviceInquiryImageCrudRepository.saveAll(it) }
            .map { it.toDomain() }
            .toList()
            .sortedBy { it.order }

    private fun partitionImages(
        existingImageList: List<ServiceInquiryImageEntity>,
        imageIdList: List<UUID>,
    ): Pair<List<ServiceInquiryImageEntity>, List<ServiceInquiryImageEntity>> =
        existingImageList.partition { image ->
            image.id !in imageIdList
        }


}
