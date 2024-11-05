package com.devooks.backend.wishlist.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.ebook.v1.service.EbookService
import com.devooks.backend.wishlist.v1.domain.Wishlist
import com.devooks.backend.wishlist.v1.dto.CreateWishlistCommand
import com.devooks.backend.wishlist.v1.dto.CreateWishlistRequest
import com.devooks.backend.wishlist.v1.dto.CreateWishlistResponse
import com.devooks.backend.wishlist.v1.dto.DeleteWishlistCommand
import com.devooks.backend.wishlist.v1.dto.DeleteWishlistResponse
import com.devooks.backend.wishlist.v1.dto.GetWishlistCommand
import com.devooks.backend.wishlist.v1.dto.GetWishlistResponse
import com.devooks.backend.wishlist.v1.dto.GetWishlistResponse.Companion.toResponse
import com.devooks.backend.wishlist.v1.service.WishlistService
import java.util.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/wishlist")
class WishlistController(
    private val wishlistService: WishlistService,
    private val ebookService: EbookService,
    private val tokenService: TokenService,
): WishlistControllerDocs {

    @Transactional
    @PostMapping
    override suspend fun createWishlist(
        @RequestBody
        request: CreateWishlistRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): CreateWishlistResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: CreateWishlistCommand = request.toCommand(requesterId)
        val ebook: Ebook = ebookService.findById(command.ebookId)
        val wishlist: Wishlist = wishlistService.create(command, ebook)
        return CreateWishlistResponse(wishlist)
    }

    @GetMapping
    override suspend fun getWishlist(
        @RequestParam(required = false, defaultValue = "")
        categoryIds: List<String>,
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): GetWishlistResponse {
        val memberId = tokenService.getMemberId(Authorization(authorization))
        val command = GetWishlistCommand(memberId, categoryIds, page, count)
        return wishlistService.get(command).toResponse()
    }

    @Transactional
    @DeleteMapping("/{wishlistId}")
    override suspend fun deleteWishlist(
        @PathVariable
        wishlistId: String,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): DeleteWishlistResponse {
        val memberId = tokenService.getMemberId(Authorization(authorization))
        val command = DeleteWishlistCommand(memberId, wishlistId)
        wishlistService.delete(command)
        return DeleteWishlistResponse()
    }

}
