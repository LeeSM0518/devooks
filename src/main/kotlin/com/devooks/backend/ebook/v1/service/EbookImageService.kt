package com.devooks.backend.ebook.v1.service

import com.devooks.backend.BackendApplication.Companion.DESCRIPTION_IMAGE_ROOT_PATH
import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.utils.saveImage
import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.dto.command.CreateEbookCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import com.devooks.backend.ebook.v1.entity.EbookImageEntity
import com.devooks.backend.ebook.v1.error.EbookError
import com.devooks.backend.ebook.v1.repository.EbookImageRepository
import java.util.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class EbookImageService(
    private val ebookImageRepository: EbookImageRepository,
) {

    suspend fun save(imageList: List<Image>, requesterId: UUID): List<EbookImage> =
        imageList
            .asFlow()
            .map {
                EbookImageEntity(
                    imagePath = saveImage(it, DESCRIPTION_IMAGE_ROOT_PATH),
                    imageOrder = it.order,
                    uploadMemberId = requesterId,
                )
            }
            .let { ebookImageRepository.saveAll(it) }
            .map { it.toDomain() }
            .toList()

    suspend fun save(descriptionImageIdList: List<UUID>, ebook: Ebook): List<EbookImage> =
        ebookImageRepository
            .findAllById(descriptionImageIdList)
            .takeIf { ebookImageList ->
                ebookImageList.filter { ebookImage ->
                    ebookImage.uploadMemberId != ebook.sellingMemberId
                }.count() == 0
            }
            ?.map { it.copy(ebookId = ebook.id) }
            ?.let { ebookImageRepository.saveAll(it) }
            ?.map { it.toDomain() }
            ?.toList()
            ?: throw EbookError.FORBIDDEN_REGISTER_EBOOK_TO_IMAGE.exception

    suspend fun modifyMainImage(command: ModifyEbookCommand) {
        val mainImageId = command.mainImageId
        if (mainImageId != null) {
            val mainImage = findById(mainImageId)
            ebookImageRepository.deleteById(mainImageId)
            ebookImageRepository.save(mainImage.copy(id = null, ebookId = command.ebookId))
        }
    }

    suspend fun modifyDescriptionImageList(command: ModifyEbookCommand, ebook: Ebook): List<EbookImage> {
        val descriptionImageList = ebookImageRepository
            .findAllByEbookId(command.ebookId)
            .filter { image -> image.id!! != ebook.mainImageId }

        val ebookImageList =
            if (command.isChangedDescriptionImageList) {
                val changeDescriptionImageIdList = command.descriptionImageIdList!!

                val (deletedImages, existImages) =
                    descriptionImageList
                        .partition { image ->
                            changeDescriptionImageIdList.all { descriptionImageId ->
                                image.id != descriptionImageId
                            }
                        }

                val newImageList =
                    changeDescriptionImageIdList.filter { change -> existImages.none { it.id == change } }
                val newEbookImageList = save(newImageList, ebook)

                ebookImageRepository.deleteAll(deletedImages)

                newEbookImageList.plus(existImages.map { it.toDomain() })
            } else {
                descriptionImageList.map { it.toDomain() }
            }
        return ebookImageList.sortedBy { it.order }
    }

    suspend fun validate(command: CreateEbookCommand) {
        findById(command.mainImageId)

        ebookImageRepository
            .findAllById(command.descriptionImageIdList)
            .takeIf { it.toList().size == command.descriptionImageIdList.size }
            ?: throw EbookError.NOT_FOUND_DESCRIPTION_IMAGE.exception
    }

    private suspend fun findById(imageId: UUID): EbookImageEntity =
        ebookImageRepository
            .findById(imageId)
            ?: throw EbookError.NOT_FOUND_MAIN_IMAGE.exception
}