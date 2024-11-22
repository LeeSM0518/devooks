package com.devooks.backend.ebook.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.ebook.v1.controller.docs.EbookImageControllerDocs
import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.dto.command.SaveImagesCommand
import com.devooks.backend.ebook.v1.dto.request.SaveDescriptionImagesRequest
import com.devooks.backend.ebook.v1.dto.request.SaveMainImageRequest
import com.devooks.backend.ebook.v1.dto.response.SaveDescriptionImagesResponse
import com.devooks.backend.ebook.v1.dto.response.SaveDescriptionImagesResponse.Companion.toSaveDescriptionImagesResponse
import com.devooks.backend.ebook.v1.dto.response.SaveMainImageResponse
import com.devooks.backend.ebook.v1.dto.response.SaveMainImageResponse.Companion.toSaveMainImageResponse
import com.devooks.backend.ebook.v1.service.EbookImageService
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ebooks")
class EbookImageController(
    private val ebookImageService: EbookImageService,
    private val tokenService: TokenService,
) : EbookImageControllerDocs {
    @Transactional
    @PostMapping("/description-images")
    override suspend fun saveDescriptionImages(
        @RequestBody
        request: SaveDescriptionImagesRequest,
        @RequestHeader(AUTHORIZATION, required = true)
        authorization: String,
    ): SaveDescriptionImagesResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: SaveImagesCommand = request.toCommand(requesterId)
        val descriptionImageList: List<EbookImage> = ebookImageService.save(command)
        return descriptionImageList.toSaveDescriptionImagesResponse()
    }

    @Transactional
    @PostMapping("/main-image")
    override suspend fun saveMainImage(
        @RequestBody
        request: SaveMainImageRequest,
        @RequestHeader(AUTHORIZATION, required = true)
        authorization: String,
    ): SaveMainImageResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: SaveImagesCommand = request.toCommand(requesterId)
        val mainImage: EbookImage = ebookImageService.save(command).first()
        return mainImage.toSaveMainImageResponse()
    }
}
