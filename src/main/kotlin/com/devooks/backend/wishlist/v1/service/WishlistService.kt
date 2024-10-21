package com.devooks.backend.wishlist.v1.service

import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.wishlist.v1.domain.Wishlist
import com.devooks.backend.wishlist.v1.dto.CreateWishlistCommand
import com.devooks.backend.wishlist.v1.dto.DeleteWishlistCommand
import com.devooks.backend.wishlist.v1.dto.GetWishlistCommand
import com.devooks.backend.wishlist.v1.entity.WishlistEntity
import com.devooks.backend.wishlist.v1.error.WishlistError
import com.devooks.backend.wishlist.v1.repository.WishlistQueryRepository
import com.devooks.backend.wishlist.v1.repository.WishlistRepository
import org.springframework.stereotype.Service

@Service
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val wishlistQueryRepository: WishlistQueryRepository,
) {
    suspend fun create(command: CreateWishlistCommand, ebook: Ebook): Wishlist {
        wishlistRepository
            .findByMemberIdAndEbookId(command.requesterId, ebook.id)
            ?.also { throw WishlistError.DUPLICATE_WISHLIST.exception }
        val wishlistEntity = WishlistEntity(memberId = command.requesterId, ebookId = ebook.id)
        return wishlistRepository.save(wishlistEntity).toDomain()
    }

    suspend fun get(command: GetWishlistCommand): List<Wishlist> =
        wishlistQueryRepository.findBy(command)

    suspend fun delete(command: DeleteWishlistCommand) {
        wishlistRepository
            .findById(command.wishlistId)
            ?.also { if (it.memberId != command.memberId) { throw WishlistError.FORBIDDEN_DELETE_WISHLIST.exception } }
            ?.also { wishlistRepository.delete(it) }
            ?: throw WishlistError.NOT_FOUND_WISHLIST.exception
    }
}