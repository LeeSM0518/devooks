package com.devooks.backend.member.v1.controller

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.member.v1.dto.GetProfileResponse
import com.devooks.backend.member.v1.dto.ModifyAccountInfoRequest
import com.devooks.backend.member.v1.dto.ModifyAccountInfoResponse
import com.devooks.backend.member.v1.dto.ModifyNicknameRequest
import com.devooks.backend.member.v1.dto.ModifyNicknameResponse
import com.devooks.backend.member.v1.dto.ModifyProfileImageRequest
import com.devooks.backend.member.v1.dto.ModifyProfileImageResponse
import com.devooks.backend.member.v1.dto.ModifyProfileRequest
import com.devooks.backend.member.v1.dto.ModifyProfileResponse
import com.devooks.backend.member.v1.dto.SignUpRequest
import com.devooks.backend.member.v1.dto.SignUpResponse
import com.devooks.backend.member.v1.dto.WithdrawMemberRequest
import com.devooks.backend.member.v1.dto.WithdrawMemberResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "회원")
interface MemberControllerDocs {

    @Operation(summary = "회원가입")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SignUpResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-1 : 인증 코드(authorizationCode)가 NULL이거나 빈 문자일 경우\n" +
                        "- AUTH-400-2 : 인증 유형(oauthType)이 NAVER, KAKAO, GOOGLE 이 아닐 경우\n" +
                        "- MEMBER-400-1 : 닉네임(nickname)이 2~12 글자가 아닐 경우\n" +
                        "- MEMBER-400-2 : 관심 카테고리(favoriteCategories)가 NULL일 경우",
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
                "- MEMBER-403-1 : 정지된 회원일 경우\n" +
                        "- MEMBER-403-2 : 탈퇴한 회원일 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "409",
                description = "- MEMBER-409-1 : 닉네임이 이미 존재할 경우",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun signUp(
        request: SignUpRequest,
    ): SignUpResponse

    @Operation(summary = "계좌 정보 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyAccountInfoResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- MEMBER-400-4 : 이름이 반드시 필요합니다.\n" +
                        "- MEMBER-400-5 : 은행이 반드시 필요합니다.\n" +
                        "- MEMBER-400-6 : 계좌번호가 반드시 필요합니다.",
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
                "- MEMBER-404-2 : 회원 정보를 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun modifyAccountInfo(
        request: ModifyAccountInfoRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): ModifyAccountInfoResponse

    @Operation(summary = "프로필 사진 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyProfileResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- COMMON-400-3 : 이미지 내용이 반드시 필요합니다.\n" +
                        "- COMMON-400-4 : 유효하지 않은 이미지 확장자입니다. JPG, PNG, JPEG만 가능합니다.\n" +
                        "- COMMON-400-5 : 50MB 이하의 영상만 저장이 가능합니다.\n" +
                        "- COMMON-400-6 : 이미지가 반드시 필요합니다.\n" +
                        "- COMMON-400-7 : 유효하지 않은 이미지 순서입니다.",
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
                "- MEMBER-404-3 : 회원을 찾을 수 없습니다.",
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
                "- COMMON-500-2 : 파일 저장을 실패했습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun modifyProfileImage(
        request: ModifyProfileImageRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): ModifyProfileImageResponse

    @Operation(summary = "닉네임 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyNicknameResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- MEMBER-400-1 : 닉네임이 반드시 필요합니다.\n" +
                        "- MEMBER-400-3 : 닉네임은 2자 이상 12자 이하만 가능합니다.",
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
                "- MEMBER-404-3 : 회원을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "409",
                description =
                "- MEMBER-409-1 : 닉네임이 이미 존재합니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun modifyNickname(
        request: ModifyNicknameRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): ModifyNicknameResponse

    @Operation(summary = "프로필 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ModifyNicknameResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- MEMBER-400-8 : 전화번호 형식(ex. 010-1234-1234)이 아닐 경우\n" +
                        "- MEMBER-400-9 : 블로그 링크가 null이 아니여 비어있을 경우\n" +
                        "- MEMBER-400-10 : 인스타그램 링크가 null이 아니여 비어있을 경우\n" +
                        "- MEMBER-400-2 : 관심 카테고리 목록이 null이 아니여 비어있을 경우\n" +
                        "- MEMBER-400-16 : 이메일이 null이 아니며 이메일 형식이 아닐 경우",
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
                "- MEMBER-404-2 : 회원 정보를 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun modifyProfile(
        request: ModifyProfileRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): ModifyProfileResponse

    @Operation(summary = "프로필 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetProfileResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- 회원 식별자가 UUID가 아닐 경우\n" +
                        "- 회원 식별자가 존재하지 않을 경우",
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
                "- MEMBER-404-2 : 회원 정보를 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun getProfile(
        @Schema(description = "회원 식별자", required = true, nullable = false)
        memberId: UUID,
    ): GetProfileResponse

    @Operation(summary = "회원 탈퇴")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = WithdrawMemberResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description =
                "- MEMBER-400-14 : 탈퇴 이유가 반드시 필요합니다.",
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
                "- MEMBER-404-3 : 회원을 찾을 수 없습니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun withdrawMember(
        request: WithdrawMemberRequest,
        @Schema(description = "액세스 토큰", required = true, nullable = false)
        authorization: String,
    ): WithdrawMemberResponse
}
