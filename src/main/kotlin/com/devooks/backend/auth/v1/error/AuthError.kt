package com.devooks.backend.auth.v1.error

import com.devooks.backend.common.exception.GeneralException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED

enum class AuthError(val exception: GeneralException) {
    // 400
    REQUIRED_AUTHORIZATION_CODE(GeneralException("AUTH-400-1", BAD_REQUEST, "인증 코드가 반드시 필요합니다.")),
    INVALID_OAUTH_TYPE(GeneralException("AUTH-400-2", BAD_REQUEST, "인증 유형은 NAVER, KAKAO, GOOGLE 만 가능합니다.")),
    REQUIRED_TOKEN(GeneralException("AUTH-400-3", BAD_REQUEST, "토큰이 반드시 필요합니다.")),
    REQUIRED_OAUTH_ID(GeneralException("AUTH-400-4", BAD_REQUEST, "인증 식별자는 반드시 필요합니다.")),

    // 401
    EXPIRED_TOKEN(GeneralException("AUTH-401-1", UNAUTHORIZED, "만료된 토큰입니다.")),
    INVALID_REFRESH_TOKEN(GeneralException("AUTH-401-2", UNAUTHORIZED, "유효하지 않는 리프래시 토큰입니다.")),
    FAILED_NAVER_OAUTH_LOGIN(GeneralException("AUTH-401-3", UNAUTHORIZED, "네이버 로그인을 실패했습니다.")),
    FAILED_KAKAO_OAUTH_LOGIN(GeneralException("AUTH-401-4", UNAUTHORIZED, "카카오 로그인을 실패했습니다.")),
    FAILED_GOOGLE_OAUTH_LOGIN(GeneralException("AUTH-401-5", UNAUTHORIZED, "구글 로그인을 실패했습니다.")),

    // 403
    UNSUPPORTED_TOKEN_FORMAT(GeneralException("AUTH-403-1", HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다.")),

    // 404
    NOT_FOUND_REFRESH_TOKEN(GeneralException("AUTH-404-1", NOT_FOUND, "리프래시 토큰이 존재하지 않습니다.")),

    // 409
    DUPLICATE_OAUTH_ID(GeneralException("AUTH-409-1", CONFLICT, "이미 존재하는 회원입니다.")),

    // 500
    FAILED_CREATE_ACCESS_TOKEN(GeneralException("AUTH-500-1", INTERNAL_SERVER_ERROR, "액세스 토큰 생성을 실패했습니다.")),
    FAILED_CREATE_REFRESH_TOKEN(GeneralException("AUTH-500-2", INTERNAL_SERVER_ERROR, "리프래시 토큰 생성을 실패했습니다.")),
    FAILED_VALIDATE_TOKEN(GeneralException("AUTH-500-3", INTERNAL_SERVER_ERROR, "토큰 검증을 실패했습니다.")),
    FAILED_SEND_EMAIL(GeneralException("AUTH-500-4", INTERNAL_SERVER_ERROR, "이메일 전송을 실패했습니다.")),
    ;

    override fun toString(): String {
        return "AuthException(exception=$exception)"
    }

}
