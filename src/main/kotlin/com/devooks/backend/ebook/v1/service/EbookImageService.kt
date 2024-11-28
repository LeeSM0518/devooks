package com.devooks.backend.ebook.v1.service

import com.devooks.backend.BackendApplication.Companion.DESCRIPTION_IMAGE_ROOT_PATH
import com.devooks.backend.BackendApplication.Companion.MAIN_IMAGE_ROOT_PATH
import com.devooks.backend.common.domain.Image
import com.devooks.backend.common.utils.saveImage
import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.domain.EbookImageType
import com.devooks.backend.ebook.v1.domain.EbookImageType.DESCRIPTION
import com.devooks.backend.ebook.v1.domain.EbookImageType.MAIN
import com.devooks.backend.ebook.v1.dto.command.CreateEbookCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import com.devooks.backend.ebook.v1.dto.command.SaveImagesCommand
import com.devooks.backend.ebook.v1.entity.EbookImageEntity
import com.devooks.backend.ebook.v1.error.EbookError
import com.devooks.backend.ebook.v1.repository.EbookImageRepository
import java.util.*
import kotlinx.coroutines.flow.Flow
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

    suspend fun save(command: SaveImagesCommand): List<EbookImage> =
        command
            .imageList
            .asFlow()
            .map {
                EbookImageEntity(
                    imagePath = saveImage(command.imageType, it),
                    imageOrder = it.order,
                    uploadMemberId = command.requesterId,
                    imageType = command.imageType,
                )
            }
            .let { ebookImageRepository.saveAll(it) }
            .map { it.toDomain() }
            .toList()

    suspend fun save(imageIdList: List<UUID>, ebook: Ebook): List<EbookImage> =
        ebookImageRepository
            .findAllById(imageIdList)
            .takeIf { ebookImageList -> validateEbookImageList(ebookImageList, ebook) }
            ?.map { it.copy(ebookId = ebook.id) }
            ?.let { ebookImageRepository.saveAll(it) }
            ?.map { it.toDomain() }
            ?.toList()
            ?: throw EbookError.FORBIDDEN_REGISTER_EBOOK_TO_IMAGE.exception

    suspend fun modifyMainImage(command: ModifyEbookCommand, ebook: Ebook): EbookImage {
        val mainImageId = command.mainImageId
        return if (mainImageId != null) {
            ebookImageRepository
                .findByEbookIdAndImageType(command.ebookId, MAIN)
                .also { ebookImage -> ebookImageRepository.delete(ebookImage) }
            save(listOf(command.mainImageId), ebook).first()
        } else {
            ebookImageRepository.findByEbookIdAndImageType(command.ebookId, MAIN).toDomain()
        }
    }

    suspend fun modifyDescriptionImageList(command: ModifyEbookCommand, ebook: Ebook): List<EbookImage> {
        val existingImageList = ebookImageRepository
            .findAllByEbookIdAndImageType(command.ebookId, DESCRIPTION)

        return command.descriptionImageIdList?.let { descriptionImageIdList ->
            val (imagesToDelete, imageToKeep) =
                partitionImages(existingImageList, descriptionImageIdList)
            val newImageIdList = descriptionImageIdList.filterNot { id -> imageToKeep.any { it.id == id } }
            save(newImageIdList, ebook)
            ebookImageRepository.deleteAll(imagesToDelete)
            updateImageOrder(descriptionImageIdList)
        } ?: existingImageList
            .map { it.toDomain() }
            .sortedBy { it.order }
    }

    suspend fun validate(command: CreateEbookCommand) {
        findById(command.mainImageId)

        ebookImageRepository
            .findAllById(command.descriptionImageIdList)
            .takeIf { it.toList().size == command.descriptionImageIdList.size }
            ?: throw EbookError.NOT_FOUND_DESCRIPTION_IMAGE.exception
    }

    private suspend fun saveImage(
        imageType: EbookImageType,
        image: Image,
    ): String {
        val rootPath = when (imageType) {
            MAIN -> MAIN_IMAGE_ROOT_PATH
            DESCRIPTION -> DESCRIPTION_IMAGE_ROOT_PATH
        }
        val savedImagePath = saveImage(image, rootPath)
        return savedImagePath
    }

    private suspend fun updateImageOrder(descriptionImageIdList: List<UUID>): List<EbookImage> =
        ebookImageRepository
            .findAllById(descriptionImageIdList)
            .map { it.copy(imageOrder = descriptionImageIdList.indexOf(it.id)) }
            .let { ebookImageRepository.saveAll(it) }
            .map { it.toDomain() }
            .toList()
            .sortedBy { it.order }

    private fun partitionImages(
        existingImageList: List<EbookImageEntity>,
        imageIdList: List<UUID>,
    ): Pair<List<EbookImageEntity>, List<EbookImageEntity>> =
        existingImageList.partition { image ->
            image.id !in imageIdList
        }

    private suspend fun validateEbookImageList(
        ebookImageList: Flow<EbookImageEntity>,
        ebook: Ebook,
    ): Boolean =
        ebookImageList.filter { ebookImage ->
            ebookImage.uploadMemberId != ebook.sellingMemberId
        }.count() == 0

    private suspend fun findById(imageId: UUID): EbookImageEntity =
        ebookImageRepository
            .findById(imageId)
            ?: throw EbookError.NOT_FOUND_MAIN_IMAGE.exception
}
