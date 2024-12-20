package com.devooks.backend.ebook.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.service.CategoryService
import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.dto.PageResponse.Companion.toResponse
import com.devooks.backend.ebook.v1.controller.docs.EbookControllerDocs
import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.domain.EbookImage
import com.devooks.backend.ebook.v1.domain.EbookOrder
import com.devooks.backend.ebook.v1.dto.EbookView
import com.devooks.backend.ebook.v1.dto.EbookView.Companion.toEbookView
import com.devooks.backend.ebook.v1.dto.command.CreateEbookCommand
import com.devooks.backend.ebook.v1.dto.command.DeleteEbookCommand
import com.devooks.backend.ebook.v1.dto.command.GetDetailOfEbookCommand
import com.devooks.backend.ebook.v1.dto.command.GetEbookCommand
import com.devooks.backend.ebook.v1.dto.command.ModifyEbookCommand
import com.devooks.backend.ebook.v1.dto.request.CreateEbookRequest
import com.devooks.backend.ebook.v1.dto.request.ModifyEbookRequest
import com.devooks.backend.ebook.v1.dto.response.CreateEbookResponse
import com.devooks.backend.ebook.v1.dto.response.DeleteEbookResponse
import com.devooks.backend.ebook.v1.dto.response.EbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetDetailOfEbookResponse
import com.devooks.backend.ebook.v1.dto.response.GetDetailOfEbookResponse.Companion.toGetDetailOfEbookResponse
import com.devooks.backend.ebook.v1.dto.response.ModifyEbookResponse
import com.devooks.backend.ebook.v1.service.EbookImageService
import com.devooks.backend.ebook.v1.service.EbookService
import com.devooks.backend.ebook.v1.service.RelatedCategoryService
import com.devooks.backend.pdf.v1.service.PdfService
import jakarta.validation.Valid
import java.util.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ebooks")
class EbookController(
    private val ebookService: EbookService,
    private val pdfService: PdfService,
    private val ebookImageService: EbookImageService,
    private val categoryService: CategoryService,
    private val relatedCategoryService: RelatedCategoryService,
    private val tokenService: TokenService,
) : EbookControllerDocs {

    @Transactional
    @PostMapping
    override suspend fun createEbook(
        @Valid
        @RequestBody
        request: CreateEbookRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): CreateEbookResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: CreateEbookCommand = request.toCommand(requesterId)
        pdfService.validate(command)
        ebookImageService.validate(command)
        val ebook: Ebook = ebookService.create(command)
        val mainImage =
            ebookImageService.save(listOf(command.mainImageId), ebook).first()
        val descriptionImageList: List<EbookImage> =
            ebookImageService.save(command.descriptionImageIdList, ebook)
        val categoryList: List<Category> = categoryService.getAll(command.relatedCategoryIdList)
        relatedCategoryService.save(categoryList, ebook)
        return CreateEbookResponse(EbookResponse(ebook, mainImage, descriptionImageList, categoryList))
    }

    @GetMapping
    override suspend fun getEbooks(
        @RequestParam
        page: Int,
        @RequestParam
        count: Int,
        @RequestParam(required = false)
        title: String?,
        @RequestParam(required = false)
        sellerMemberId: UUID?,
        @RequestParam(required = false)
        ebookIdList: List<UUID>?,
        @RequestParam(required = false)
        categoryIdList: List<UUID>?,
        @RequestParam(required = false)
        orderBy: EbookOrder?,
        @RequestHeader(AUTHORIZATION)
        authorization: String?,
    ): PageResponse<EbookView> {
        val requesterId = authorization?.let { tokenService.getMemberId(Authorization(it)) }
        val command = GetEbookCommand(
            title = title,
            sellerMemberId = sellerMemberId,
            ebookIdList = ebookIdList,
            categoryIdList = categoryIdList,
            orderBy = orderBy,
            requesterId = requesterId,
            page = page,
            count = count
        )
        val ebooks = ebookService.get(command)
        return ebooks.map { it.toEbookView() }.toResponse()
    }

    @GetMapping("/{ebookId}")
    override suspend fun getDetailOfEbook(
        @PathVariable("ebookId")
        ebookId: UUID,
        @RequestHeader(AUTHORIZATION, required = false)
        authorization: String?,
    ): GetDetailOfEbookResponse {
        val requesterId = authorization?.let { tokenService.getMemberId(Authorization(it)) }
        val command = GetDetailOfEbookCommand(ebookId, requesterId)
        return ebookService.get(command).toGetDetailOfEbookResponse()
    }

    @Transactional
    @PatchMapping("/{ebookId}")
    override suspend fun modifyEbook(
        @PathVariable("ebookId")
        ebookId: UUID,
        @Valid
        @RequestBody
        request: ModifyEbookRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyEbookResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyEbookCommand = request.toCommand(ebookId, requesterId)
        val ebook: Ebook = ebookService.modify(command)
        val mainImage = ebookImageService.modifyMainImage(command, ebook)
        val descriptionImageList: List<EbookImage> = ebookImageService.modifyDescriptionImageList(command, ebook)
        val categoryList: List<Category> = relatedCategoryService.modify(command, ebook)
        return ModifyEbookResponse(EbookResponse(ebook, mainImage, descriptionImageList, categoryList))
    }

    @Transactional
    @DeleteMapping("/{ebookId}")
    override suspend fun deleteEbook(
        @PathVariable("ebookId")
        ebookId: UUID,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): DeleteEbookResponse {
        val requesterId = tokenService.getMemberId(Authorization(authorization))
        val command = DeleteEbookCommand(ebookId, requesterId)
        ebookService.delete(command)
        return DeleteEbookResponse()
    }

}
