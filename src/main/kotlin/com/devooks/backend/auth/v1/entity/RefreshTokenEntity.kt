package com.devooks.backend.auth.v1.entity

import com.devooks.backend.auth.v1.domain.RefreshToken
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "refresh_token")
data class RefreshTokenEntity(
    @Id
    @Column("refresh_token_id")
    @get:JvmName("refreshTokenId")
    val id: UUID? = null,
    val memberId: UUID,
    val token: RefreshToken,
    val registeredDate: Instant = Instant.now(),
    val modifiedDate: Instant = registeredDate,
) : Persistable<UUID> {

    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun update(token: RefreshToken): RefreshTokenEntity =
        this.copy(
            token = token,
            modifiedDate = Instant.now()
        )
}
