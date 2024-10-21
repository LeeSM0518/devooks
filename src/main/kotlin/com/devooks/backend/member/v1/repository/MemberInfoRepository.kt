package com.devooks.backend.member.v1.repository

import com.devooks.backend.member.v1.entity.MemberInfoEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MemberInfoRepository : CoroutineCrudRepository<MemberInfoEntity, UUID> {

    suspend fun findByMemberId(memberId: UUID): MemberInfoEntity?
}
