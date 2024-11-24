package com.devooks.backend.member.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class MemberError(val exception: GeneralException) {
    // 400
    INVALID_MEMBER_ID(GeneralException("MEMBER-400-15", BAD_REQUEST, "잘못된 형식의 회원 식별자 입니다.")),
    REQUIRED_EMAIL(GeneralException("MEMBER-400-16", BAD_REQUEST, "이메일은 반드시 필요합니다.")),
    INVALID_EMAIL(GeneralException("MEMBER-400-17", BAD_REQUEST, "잘못된 형식의 이메일 입니다.")),

    // 403
    SUSPENDED_MEMBER(GeneralException("MEMBER-403-1", FORBIDDEN, "정지된 회원으로 서비스 이용이 불가합니다.")),
    WITHDREW_MEMBER(GeneralException("MEMBER-403-2", FORBIDDEN, "탈퇴한 회원으로 계정 복구가 필요합니다.")),

    // 404
    NOT_FOUND_OAUTH_INFO_BY_EMAIL(GeneralException("MEMBER-404-1", NOT_FOUND, "")),
    NOT_FOUND_MEMBER_INFO_BY_ID(GeneralException("MEMBER-404-2", NOT_FOUND, "회원 정보를 찾을 수 없습니다.")),
    NOT_FOUND_MEMBER_BY_ID(GeneralException("MEMBER-404-3", NOT_FOUND, "회원을 찾을 수 없습니다.")),

    // 409
    DUPLICATE_NICKNAME(GeneralException("MEMBER-409-1", CONFLICT, "닉네임이 이미 존재합니다.")),
    ;

    override fun toString(): String {
        return "MemberError(exception=$exception)"
    }
}
