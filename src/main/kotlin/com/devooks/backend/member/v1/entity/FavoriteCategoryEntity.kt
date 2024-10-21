package com.devooks.backend.member.v1.entity

import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "favorite_category")
data class FavoriteCategoryEntity(
    @Id
    @Column("favorite_category_id")
    @get:JvmName("favoriteCategoryId")
    val id: UUID? = null,
    val favoriteMemberId: UUID,
    val categoryId: UUID,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null
}