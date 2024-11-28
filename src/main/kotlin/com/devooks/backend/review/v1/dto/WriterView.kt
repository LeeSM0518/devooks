package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import java.util.*

data class WriterView(
    val memberId: UUID,
    val nickname: String,
    val profileImagePath: String,
) {
    companion object {
        fun ReviewRow.toWriterView() =
            WriterView(
                memberId = this.memberId,
                nickname = this.nickname,
                profileImagePath = this.profileImagePath ?: "",
            )

        fun Member.toWriterView() =
            WriterView(
                memberId = this.id,
                nickname = this.nickname,
                profileImagePath = this.profileImagePath,
            )
    }
}
