package com.devooks.backend.member.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class MemberError(val exception: GeneralException) {
    // 400
    REQUIRED_NICKNAME(GeneralException("MEMBER-400-1", BAD_REQUEST, "닉네임이 반드시 필요합니다.")),
    REQUIRED_FAVORITE_CATEGORIES(GeneralException("MEMBER-400-2", BAD_REQUEST, "관심 카테고리 목록이 반드시 필요합니다.")),
    INVALID_NICKNAME(GeneralException("MEMBER-400-3", BAD_REQUEST, "닉네임은 2자 이상 12자 이하만 가능합니다.")),
    REQUIRED_REAL_NAME(GeneralException("MEMBER-400-4", BAD_REQUEST, "이름이 반드시 필요합니다.")),
    REQUIRED_BANK(GeneralException("MEMBER-400-5", BAD_REQUEST, "은행이 반드시 필요합니다.")),
    REQUIRED_ACCOUNT_NUMBER(GeneralException("MEMBER-400-6", BAD_REQUEST, "계좌번호가 반드시 필요합니다.")),
    REQUIRED_PHONE_NUMBER(GeneralException("MEMBER-400-7", BAD_REQUEST, "전화번호가 반드시 필요합니다.")),
    INVALID_PHONE_NUMBER(GeneralException("MEMBER-400-8", BAD_REQUEST, "잘못된 형식의 전화번호 입니다.")),
    REQUIRED_BLOG_LINK(GeneralException("MEMBER-400-9", BAD_REQUEST, "블로그 링크가 반드시 필요합니다.")),
    REQUIRED_INSTAGRAM_LINK(GeneralException("MEMBER-400-10", BAD_REQUEST, "인스타그램 링크가 반드시 필요합니다.")),
    REQUIRED_YOUTUBE_LINK(GeneralException("MEMBER-400-11", BAD_REQUEST, "유튜브 링크가 반드시 필요합니다.")),
    REQUIRED_INTRODUCTION_LINK(GeneralException("MEMBER-400-12", BAD_REQUEST, "소개글이 반드시 필요합니다.")),
    INVALID_FAVORITE_CATEGORIES(GeneralException("MEMBER-400-13", BAD_REQUEST, "잘못된 형식의 카테고리 식별자 입니다.")),
    REQUIRED_WITHDRAWAL_REASON(GeneralException("MEMBER-400-14", BAD_REQUEST, "탈퇴 이유가 반드시 필요합니다.")),
    INVALID_MEMBER_ID(GeneralException("MEMBER-400-15", BAD_REQUEST, "잘못된 형식의 회원 식별자 입니다.")),
    REQUIRED_EMAIL(GeneralException("MEMBER-400-16", BAD_REQUEST, "이메일은 반드시 필요합니다.")),
    INVALID_EMAIL(GeneralException("MEMBER-400-17", BAD_REQUEST, "잘못도니 형식의 이메일 입니다.")),

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
