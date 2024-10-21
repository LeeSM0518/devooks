package com.devooks.backend.member.v1.repository

import com.devooks.backend.member.v1.entity.MemberEntity
import java.util.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : CoroutineCrudRepository<MemberEntity, UUID> {

    suspend fun findByNickname(nickname: String): MemberEntity?
}
