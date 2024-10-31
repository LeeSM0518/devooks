package com.devooks.backend.member.v1.entity

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

    fun update(command: ModifyProfileCommand) =
        copy(
            phoneNumber = command.phoneNumber ?: this.phoneNumber,
            blogLink = command.blogLink ?: this.blogLink,
            instagramLink = command.instagramLink ?: this.instagramLink,
            youtubeLink = command.youtubeLink ?: this.youtubeLink,
            introduction = command.introduction ?: this.introduction,
            email = command.email ?: this.email,
        )
}
