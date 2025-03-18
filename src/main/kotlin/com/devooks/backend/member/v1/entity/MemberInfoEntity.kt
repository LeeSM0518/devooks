package com.devooks.backend.member.v1.entity

import com.devooks.backend.member.v1.dto.ModifyAccountInfoCommand
import com.devooks.backend.member.v1.dto.ModifyProfileCommand
import java.util.*
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "member_info")
data class MemberInfoEntity(
    @Id
    @Column("member_info_id")
    @get:JvmName("memberInfoId")
    val id: UUID? = null,
    val memberId: UUID,
    val blogLink: String = "",
    val instagramLink: String = "",
    val youtubeLink: String = "",
    val realName: String = "",
    val bank: String = "",
    val accountNumber: String = "",
    val introduction: String = "",
    val phoneNumber: String = "",
    val email: String = "",
) : Persistable<UUID> {
    override fun getId(): UUID? = id

    override fun isNew(): Boolean = id == null

    fun updateProfile(command: ModifyProfileCommand) =
        copy(
            phoneNumber = command.phoneNumber,
            blogLink = command.blogLink,
            instagramLink = command.instagramLink,
            youtubeLink = command.youtubeLink,
            introduction = command.introduction,
            email = command.email,
        )

    fun updateAccount(command: ModifyAccountInfoCommand) =
        copy(
            realName = command.realName ?: this.realName,
            bank = command.bank ?: this.bank,
            accountNumber = command.accountNumber ?: this.accountNumber,
        )
}
