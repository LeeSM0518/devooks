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
        val serviceInquiryImageList =
            serviceInquiryImageCrudRepository
                .findAllByServiceInquiryId(command.serviceInquiryId)

        val changedServiceInquiryImageList =
            if (command.isChangedImageList) {
                val changeImageIdList = command.imageIdList!!

                val (deletedImageList, existImageList) =
                    serviceInquiryImageList
                        .partition { image ->
                            changeImageIdList.all { imageId ->
                                image.id != imageId
                            }
                        }

                val newImageList = changeImageIdList.filter { change -> existImageList.none { it.id == change } }
                val newServiceInquiryImageList = save(newImageList, serviceInquiry)

                serviceInquiryImageCrudRepository.deleteAll(deletedImageList)

                newServiceInquiryImageList.plus(existImageList.map { it.toDomain() })
            } else {
                serviceInquiryImageList.map { it.toDomain() }
            }
        return changedServiceInquiryImageList.sortedBy { it.order }
    }


}
