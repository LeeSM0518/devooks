package com.devooks.backend.member.v1.service

import com.devooks.backend.BackendApplication.Companion.PROFILE_IMAGE_ROOT_PATH
import com.devooks.backend.auth.v1.domain.OauthInfo
import com.devooks.backend.auth.v1.domain.OauthType
import com.devooks.backend.auth.v1.entity.OauthInfoEntity
import com.devooks.backend.auth.v1.error.AuthError
import com.devooks.backend.auth.v1.repository.OauthInfoRepository
import com.devooks.backend.common.utils.saveImage
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.Member.Companion.toDomain
import com.devooks.backend.member.v1.dto.ModifyNicknameCommand
import com.devooks.backend.member.v1.dto.ModifyProfileImageCommand
import com.devooks.backend.member.v1.dto.SignUpCommand
import com.devooks.backend.member.v1.dto.WithdrawMemberCommand
import com.devooks.backend.member.v1.entity.MemberEntity
import com.devooks.backend.member.v1.error.MemberError
import com.devooks.backend.member.v1.repository.MemberRepository
import java.time.Instant
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val oauthInfoRepository: OauthInfoRepository,
) {

    @Transactional
    suspend fun signUp(command: SignUpCommand): Member {
        validateOauthInfo(command.oauthId, command.oauthType)
        validateNickname(command.nickname)
        val member = MemberEntity(nickname = command.nickname)
        val savedMember = memberRepository.save(member)
        val oauthInfo = OauthInfoEntity(
            oauthId = command.oauthId,
            oauthType = command.oauthType,
            memberId = savedMember.id!!
        )
        oauthInfoRepository.save(oauthInfo)
        return savedMember.toDomain()
    }

    suspend fun findByOauthInfo(oauthInfo: OauthInfo): Member {
        val foundOauthInfo = oauthInfoRepository
            .findByOauthIdAndOauthType(oauthInfo.oauthId, oauthInfo.oauthType)
            ?: throw MemberError
                .NOT_FOUND_OAUTH_INFO_BY_EMAIL
                .exception
                .copy(message = "{\"oauthId\":\"${oauthInfo.oauthId}\"}")

        return memberRepository
            .findById(foundOauthInfo.memberId)
            ?.also { member -> validateMember(member) }
            ?.toDomain()
            ?: throw MemberError
                .NOT_FOUND_OAUTH_INFO_BY_EMAIL
                .exception
                .copy(message = "{\"oauthId\":\"${foundOauthInfo.oauthId}\"}")
    }

    suspend fun updateProfileImage(
        command: ModifyProfileImageCommand,
        requesterId: UUID,
    ): Member {
        val member = findMemberById(requesterId)
        val image = command.image
        val path = saveImage(image, PROFILE_IMAGE_ROOT_PATH)
        val updatedMember = member.copy(profileImagePath = path)
        return memberRepository.save(updatedMember).toDomain()
    }

    suspend fun updateNickname(
        command: ModifyNicknameCommand,
        requesterId: UUID,
    ): Member {
        validateNickname(command.nickname)
        val member = findMemberById(requesterId)
        val updatedMember = member.copy(nickname = command.nickname)
        return memberRepository.save(updatedMember).toDomain()
    }

    suspend fun findById(memberId: UUID): Member = findMemberById(memberId).toDomain()

    suspend fun withdraw(command: WithdrawMemberCommand, requesterId: UUID) {
        findMemberById(requesterId)
            .copy(withdrawalDate = Instant.now())
            .also { updateMember -> memberRepository.save(updateMember) }
    }

    private suspend fun validateOauthInfo(oauthId: String, oauthType: OauthType) {
        oauthInfoRepository
            .findByOauthIdAndOauthType(oauthId, oauthType)
            ?.let { oauthInfo ->
                val member = findMemberById(oauthInfo.memberId)
                validateMember(member)
                throw AuthError.DUPLICATE_OAUTH_ID.exception
            }
    }

    private suspend fun validateMember(member: MemberEntity) {
        val now = Instant.now()

        if (member.untilSuspensionDate != null && now.isBefore(member.untilSuspensionDate)) {
            throw MemberError.SUSPENDED_MEMBER.exception
        } else if (member.withdrawalDate != null) {
            throw MemberError.WITHDREW_MEMBER.exception
        }
    }

    private suspend fun findMemberById(requesterId: UUID) =
        memberRepository
            .findById(requesterId)
            ?: throw MemberError.NOT_FOUND_MEMBER_BY_ID.exception

    private suspend fun validateNickname(nickname: String) {
        memberRepository
            .findByNickname(nickname)
            ?.let { throw MemberError.DUPLICATE_NICKNAME.exception }
    }

}