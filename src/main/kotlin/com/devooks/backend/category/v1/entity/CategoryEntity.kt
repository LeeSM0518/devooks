package com.devooks.backend.category.v1.entity

import java.time.Instant
import java.time.Instant.now
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "category")
data class CategoryEntity(
    @Id
    @Column("category_id")
    @get:JvmName("categoryId")
    val id: UUID? = null,
    val name: String,
    val registeredDate: Instant = now(),
    val modifiedDate: Instant = registeredDate,
    val deletedDate: Instant? = null,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null
}
