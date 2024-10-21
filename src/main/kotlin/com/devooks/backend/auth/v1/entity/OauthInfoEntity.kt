package com.devooks.backend.auth.v1.entity

import com.devooks.backend.auth.v1.domain.OauthId
import com.devooks.backend.auth.v1.domain.OauthType
import java.time.Instant
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table(value = "oauth_info")
data class OauthInfoEntity(
    @Id
    val oauthId: OauthId,
    val oauthType: OauthType,
    val memberId: UUID,
    val registeredDate: Instant? = null
) : Persistable<OauthId> {
    override fun getId(): OauthId = oauthId

    override fun isNew(): Boolean = registeredDate == null
}
