package com.devooks.backend.member.v1.controller

import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.member.v1.dto.GetProfileResponse
import com.devooks.backend.member.v1.dto.ModifyAccountInfoRequest
import com.devooks.backend.member.v1.dto.ModifyAccountInfoResponse
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

@Tag(name = "Member", description = "회원")
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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
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
                        schema = Schema(implementation = ModifyProfileImageResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): ModifyProfileImageResponse

    @Operation(summary = "프로필 수정")
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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): ModifyProfileResponse

    @Operation(summary = "프로필 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK, 액세스 토큰이 존재하지 않을 경우 " +
                        "개인정보(bank, accountNumber, introduction, phoneNumber, email)를 null로 반환",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetProfileResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "회원 식별자", required = true, implementation = UUID::class)
        memberId: UUID,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", nullable = true)
        authorization: String,
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
                description = "- COMMON-400-0 : 유효하지 않은 요청입니다.",
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
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): WithdrawMemberResponse
}
