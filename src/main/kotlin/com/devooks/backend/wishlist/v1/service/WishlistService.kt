package com.devooks.backend.wishlist.v1.service

import com.devooks.backend.ebook.v1.domain.Ebook
import com.devooks.backend.wishlist.v1.domain.Wishlist
import com.devooks.backend.wishlist.v1.dto.CreateWishlistCommand
import com.devooks.backend.wishlist.v1.dto.DeleteWishlistCommand
import com.devooks.backend.wishlist.v1.dto.GetWishlistCommand
import com.devooks.backend.wishlist.v1.entity.WishlistEntity
import com.devooks.backend.wishlist.v1.error.WishlistError
import com.devooks.backend.wishlist.v1.repository.WishlistCrudRepository
import com.devooks.backend.wishlist.v1.repository.WishlistQueryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class WishlistService(
    private val wishlistCrudRepository: WishlistCrudRepository,
    private val wishlistQueryRepository: WishlistQueryRepository,
) {
    suspend fun create(command: CreateWishlistCommand, ebook: Ebook): Wishlist {
        wishlistCrudRepository
            .findByMemberIdAndEbookId(command.requesterId, ebook.id)
            ?.also { throw WishlistError.DUPLICATE_WISHLIST.exception }
        val wishlistEntity = WishlistEntity(memberId = command.requesterId, ebookId = ebook.id)
        return wishlistCrudRepository.save(wishlistEntity).toDomain()
    }

    suspend fun get(command: GetWishlistCommand): Page<Wishlist> {
        val wishlists = wishlistQueryRepository.findBy(command)
        val count = wishlistQueryRepository.countBy(command)
        return PageImpl(wishlists.toList(), command.pageable, count.first())
    }

    suspend fun delete(command: DeleteWishlistCommand) {
        wishlistCrudRepository
            .findById(command.wishlistId)
            ?.also {
                if (it.memberId != command.memberId) {
                    throw WishlistError.FORBIDDEN_DELETE_WISHLIST.exception
                }
            }
            ?.also { wishlistCrudRepository.delete(it) }
            ?: throw WishlistError.NOT_FOUND_WISHLIST.exception
    }
}
