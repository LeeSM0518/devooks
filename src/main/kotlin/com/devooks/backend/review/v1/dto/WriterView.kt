package com.devooks.backend.review.v1.dto

import com.devooks.backend.member.v1.domain.Member
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class WriterView(
    @Schema(description = "작성자 회원 식별자")
    val memberId: UUID,
    @Schema(description = "닉네임")
    val nickname: String,
    @Schema(description = "프로필 사진 경로")
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

        fun ReviewCommentRow.toWriterView() =
            WriterView(
                memberId = this.memberId,
                nickname = this.nickname,
                profileImagePath = this.profileImagePath ?: ""
            )
    }
}
