package com.devooks.backend.ebook.v1.entity

import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "related_category")
data class RelatedCategoryEntity(
    @Id
    @Column("related_category_id")
    @get:JvmName("relatedCategoryId")
    val id: UUID? = null,
    val ebookId: UUID,
    val categoryId: UUID,
): Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null
}
