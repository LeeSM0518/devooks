package com.devooks.backend.member.v1.entity

import com.devooks.backend.auth.v1.domain.Authority
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "member")
data class MemberEntity(
    @Id
    @Column("member_id")
    @get:JvmName("memberId")
    val id: UUID? = null,
    val profileImagePath: String? = null,
    val nickname: String,
    val authority: Authority = Authority.USER,
    val withdrawalDate: Instant? = null,
    val untilSuspensionDate: Instant? = null,
    val registeredDate: Instant = Instant.now(),
    val modifiedDate: Instant = registeredDate,
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null
}
