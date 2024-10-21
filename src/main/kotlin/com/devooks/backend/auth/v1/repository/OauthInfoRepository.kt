package com.devooks.backend.auth.v1.repository

import com.devooks.backend.auth.v1.domain.OauthId
import com.devooks.backend.auth.v1.domain.OauthType
import com.devooks.backend.auth.v1.entity.OauthInfoEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OauthInfoRepository : CoroutineCrudRepository<OauthInfoEntity, OauthId> {

    suspend fun findByOauthIdAndOauthType(oauthId: OauthId, oauthType: OauthType): OauthInfoEntity?
}
