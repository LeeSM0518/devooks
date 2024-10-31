package com.devooks.backend.member.v1.service

import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.MemberInfo
import com.devooks.backend.member.v1.domain.MemberInfo.Companion.toDomain
import com.devooks.backend.member.v1.dto.ModifyAccountInfoCommand
import com.devooks.backend.member.v1.dto.ModifyProfileCommand
import com.devooks.backend.member.v1.entity.MemberInfoEntity
import com.devooks.backend.member.v1.error.MemberError
import com.devooks.backend.member.v1.repository.MemberInfoRepository
import java.util.*
import org.springframework.stereotype.Service

@Service
class MemberInfoService(
    private val memberInfoRepository: MemberInfoRepository,
) {

    suspend fun create(member: Member): MemberInfo {
        val entity = MemberInfoEntity(memberId = member.id)
        val memberInfo = memberInfoRepository.save(entity).toDomain()
        return memberInfo
    }

    suspend fun updateAccountInfo(
        command: ModifyAccountInfoCommand,
        requesterId: UUID,
    ): MemberInfo {
        val memberInfo =
            memberInfoRepository
                .findByMemberId(requesterId)
                ?: throw MemberError.NOT_FOUND_MEMBER_INFO_BY_ID.exception
        val updatedMemberInfo =
            memberInfo.copy(accountNumber = command.accountNumber, bank = command.bank, realName = command.realName)
        val savedMemberInfo = memberInfoRepository.save(updatedMemberInfo)
        return savedMemberInfo.toDomain()
    }

    suspend fun findById(memberId: UUID): MemberInfo =
        findMemberInfoById(memberId).toDomain()

    suspend fun updateProfile(
        command: ModifyProfileCommand,
        requesterId: UUID,
    ): MemberInfo {
        val memberInfo = findMemberInfoById(requesterId)
        val updateMemberInfo = memberInfo.update(command)
        return memberInfoRepository.save(updateMemberInfo).toDomain()
    }

    private suspend fun findMemberInfoById(requesterId: UUID) =
        memberInfoRepository
            .findByMemberId(requesterId)
            ?: throw MemberError.NOT_FOUND_MEMBER_INFO_BY_ID.exception

}
