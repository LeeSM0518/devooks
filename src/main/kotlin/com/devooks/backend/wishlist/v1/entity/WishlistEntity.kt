package com.devooks.backend.wishlist.v1.entity

import com.devooks.backend.wishlist.v1.domain.Wishlist
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "wishlist")
data class WishlistEntity(
    @Id
    @Column(value = "wishlist_id")
    @get:JvmName("wishlistId")
    val id: UUID? = null,
    val memberId: UUID,
    val ebookId: UUID,
    val createdDate: Instant = Instant.now(),
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun toDomain() =
        Wishlist(
            id = this.id!!,
            memberId = this.memberId,
            ebookId = this.ebookId,
            createdDate = this.createdDate
        )
}