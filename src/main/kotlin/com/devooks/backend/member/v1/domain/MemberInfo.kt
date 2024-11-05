package com.devooks.backend.member.v1.domain

import com.devooks.backend.member.v1.entity.MemberInfoEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

class MemberInfo(
    @Schema(description = "회원 정보 식별자")
    val id: UUID,
    @Schema(description = "회원 식별자")
    val memberId: UUID,
    @Schema(description = "블로그 링크")
    val blogLink: String,
    @Schema(description = "인스타그램 링크")
    val instagramLink: String,
    @Schema(description = "유튜브 링크")
    val youtubeLink: String,
    @Schema(description = "수취인 이름")
    val realName: String,
    @Schema(description = "은행명")
    val bank: String,
    @Schema(description = "계좌번호")
    val accountNumber: String,
    @Schema(description = "소개글")
    val introduction: String,
    @Schema(description = "전화번호")
    val phoneNumber: String,
    @Schema(description = "이메일")
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
