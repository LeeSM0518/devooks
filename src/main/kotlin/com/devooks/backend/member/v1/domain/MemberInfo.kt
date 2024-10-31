package com.devooks.backend.member.v1.domain

import com.devooks.backend.member.v1.entity.MemberInfoEntity
import java.util.*

class MemberInfo(
    val id: UUID,
    val memberId: UUID,
    val blogLink: String,
    val instagramLink: String,
    val youtubeLink: String,
    val realName: String,
    val bank: String,
    val accountNumber: String,
    val introduction: String,
    val phoneNumber: String,
    val email: String,
) {
    companion object {
        fun MemberInfoEntity.toDomain(): MemberInfo =
            MemberInfo(
                id = this.id!!,
                memberId = this.memberId,
                blogLink = this.blogLink,
                instagramLink = this.instagramLink,
                youtubeLink = this.youtubeLink,
                realName = this.realName,
                bank = this.bank,
                accountNumber = this.accountNumber,
                introduction = this.introduction,
                phoneNumber = this.phoneNumber,
                email = this.email,
            )
    }
}
