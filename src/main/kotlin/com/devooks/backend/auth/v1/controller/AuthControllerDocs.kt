package com.devooks.backend.auth.v1.controller

import com.devooks.backend.auth.v1.dto.CheckEmailRequest
import com.devooks.backend.auth.v1.dto.CheckEmailResponse
import com.devooks.backend.auth.v1.dto.LoginRequest
import com.devooks.backend.auth.v1.dto.LoginResponse
import com.devooks.backend.auth.v1.dto.LogoutRequest
import com.devooks.backend.auth.v1.dto.LogoutResponse
import com.devooks.backend.auth.v1.dto.ReissueRequest
import com.devooks.backend.auth.v1.dto.ReissueResponse
import com.devooks.backend.common.exception.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "인증")
interface AuthControllerDocs {

    @Operation(summary = "로그인")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = LoginResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-1 : 인증 코드(authorizationCode)가 NULL이거나 빈 문자일 경우\n" +
                        "- AUTH-400-2 : 인증 유형(oauthType)이 NAVER, KAKAO, GOOGLE 이 아닐 경우 ",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "401",
                description =
                "- AUTH-401-3 : 네이버 로그인을 실패할 경우\n" +
                        "- AUTH-401-4 : 카카오 로그인을 실패할 경우\n" +
                        "- AUTH-401-5 : 구글 로그인을 실패할 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- AUTH-403-1 : 정지된 회원일 경우 경우\n" +
                        "- AUTH-403-2 : 탈퇴한 회원일 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- MEMBER-404-1 : 회원을 찾을 수 없는 경우 " +
                        "(message에 oauthId를 넣어서 응답, {\"oauthId\" : \":oauthId\"})",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun login(request: LoginRequest): LoginResponse


    @Operation(summary = "로그아웃")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = LogoutResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-3 : 리프래시 토큰(refreshToken)이 NULL이거나 빈 문자일 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "401",
                description =
                "- AUTH-401-1 : 기간이 만료된 토큰일 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- AUTH-403-1 : 잘못된 형식의 토큰일 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun logout(request: LogoutRequest): LogoutResponse

    @Operation(summary = "토큰 재발급")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ReissueResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-3 : 리프래시 토큰(refreshToken)이 NULL이거나 빈 문자일 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "401",
                description =
                "- AUTH-401-1 : 기간이 만료된 토큰일 경우\n" +
                        "- AUTH-401-2 : 원본 리프래시 토큰과 일치하지 않을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- AUTH-403-1 : 잘못된 형식의 토큰일 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "404",
                description =
                "- AUTH-404-1 : 리프래시 토큰이 존재하지 않을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun reissue(request: ReissueRequest): ReissueResponse

    @Operation(summary = "이메일 확인")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CheckEmailResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-16 : 이메일 값이 존재하지 않을 경우" +
                        "- AUTH-400-17 : 이메일 형식이 아닐 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "500",
                description =
                "- AUTH-500-4 : 이메일 전송을 실패했을 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun checkEmail(request: CheckEmailRequest): CheckEmailResponse
}
