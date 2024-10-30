package com.devooks.backend.member.v1.controller

import com.devooks.backend.auth.v1.domain.Authorization
import com.devooks.backend.auth.v1.service.TokenService
import com.devooks.backend.category.v1.domain.Category
import com.devooks.backend.category.v1.service.CategoryService
import com.devooks.backend.member.v1.domain.Member
import com.devooks.backend.member.v1.domain.MemberInfo
import com.devooks.backend.member.v1.dto.GetProfileResponse
import com.devooks.backend.member.v1.dto.ModifyAccountInfoCommand
import com.devooks.backend.member.v1.dto.ModifyAccountInfoRequest
import com.devooks.backend.member.v1.dto.ModifyAccountInfoResponse
import com.devooks.backend.member.v1.dto.ModifyNicknameCommand
import com.devooks.backend.member.v1.dto.ModifyNicknameRequest
import com.devooks.backend.member.v1.dto.ModifyNicknameResponse
import com.devooks.backend.member.v1.dto.ModifyProfileCommand
import com.devooks.backend.member.v1.dto.ModifyProfileImageCommand
import com.devooks.backend.member.v1.dto.ModifyProfileImageRequest
import com.devooks.backend.member.v1.dto.ModifyProfileImageResponse
import com.devooks.backend.member.v1.dto.ModifyProfileRequest
import com.devooks.backend.member.v1.dto.ModifyProfileResponse
import com.devooks.backend.member.v1.dto.SignUpRequest
import com.devooks.backend.member.v1.dto.SignUpResponse
import com.devooks.backend.member.v1.dto.WithdrawMemberCommand
import com.devooks.backend.member.v1.dto.WithdrawMemberRequest
import com.devooks.backend.member.v1.dto.WithdrawMemberResponse
import com.devooks.backend.member.v1.service.FavoriteCategoryService
import com.devooks.backend.member.v1.service.MemberInfoService
import com.devooks.backend.member.v1.service.MemberService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "회원 API")
@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService,
    private val memberInfoService: MemberInfoService,
    private val categoryService: CategoryService,
    private val favoriteCategoryService: FavoriteCategoryService,
    private val tokenService: TokenService,
) {

    @Operation(summary = "회원가입")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(
                responseCode = "400",
                description =
                "- AUTH-400-1 : 인증 코드(authorizationCode)가 NULL이거나 빈 문자일 경우\n" +
                        "- AUTH-400-2 : 인증 유형(oauthType)이 NAVER, KAKAO, GOOGLE 이 아닐 경우\n" +
                        "- MEMBER-400-1 : 닉네임(nickname)이 2~12 글자가 아닐 경우\n" +
                        "- MEMBER-400-2 : 관심 카테고리(favoriteCategories)가 NULL일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "403",
                description =
                "- MEMBER-403-1 : 정지된 회원일 경우\n" +
                        "- MEMBER-403-2 : 탈퇴한 회원일 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            ),
            ApiResponse(
                responseCode = "409",
                description = "- MEMBER-409-1 : 닉네임이 이미 존재할 경우",
                content = arrayOf(Content(schema = Schema(hidden = true)))
            )
        ]
    )
    @Transactional
    @PostMapping("/signup")
    suspend fun signUp(
        @RequestBody
        request: SignUpRequest,
    ): SignUpResponse {
        val command = request.toCommand()
        val member = memberService.signUp(command)
        memberInfoService.create(member)
        val categories = categoryService.getAll(command.favoriteCategoryIdList)
        favoriteCategoryService.save(categories, member.id)
        val tokenGroup = tokenService.createTokenGroup(member)
        return SignUpResponse(
            member = SignUpResponse.Member(member),
            tokenGroup = tokenGroup
        )
    }

    @Transactional
    @PatchMapping("/account")
    suspend fun modifyAccountInfo(
        @RequestBody
        request: ModifyAccountInfoRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyAccountInfoResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyAccountInfoCommand = request.toCommand()
        val memberInfo: MemberInfo = memberInfoService.updateAccountInfo(command, requesterId)
        return ModifyAccountInfoResponse(memberInfo)
    }

    @Transactional
    @PatchMapping("/image")
    suspend fun modifyProfileImage(
        @RequestBody
        request: ModifyProfileImageRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyProfileImageResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyProfileImageCommand = request.toCommand()
        val member: Member = memberService.updateProfileImage(command, requesterId)
        return ModifyProfileImageResponse(member)
    }

    @Transactional
    @PatchMapping("/nickname")
    suspend fun modifyNickname(
        @RequestBody
        request: ModifyNicknameRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyNicknameResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyNicknameCommand = request.toCommand()
        val member: Member = memberService.updateNickname(command, requesterId)
        return ModifyNicknameResponse(member)
    }

    @Transactional
    @PatchMapping("/profile")
    suspend fun modifyProfile(
        @RequestBody
        request: ModifyProfileRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): ModifyProfileResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyProfileCommand = request.toCommand()
        val memberInfo: MemberInfo = memberInfoService.updateProfile(command, requesterId)
        val categories: List<Category> = categoryService.getAll(command.favoriteCategoryIdList)
        favoriteCategoryService.deleteByMemberId(requesterId)
        favoriteCategoryService.save(categories, requesterId)
        return ModifyProfileResponse(memberInfo, categories)
    }

    @GetMapping("/{memberId}/profile")
    suspend fun getProfile(
        @PathVariable
        memberId: UUID,
    ): GetProfileResponse {
        val member: Member = memberService.findById(memberId)
        val memberInfo: MemberInfo = memberInfoService.findById(memberId)
        val categories: List<Category> = favoriteCategoryService.findByMemberId(memberId)
        return GetProfileResponse(member, memberInfo, categories)
    }

    @Transactional
    @PatchMapping("/withdrawal")
    suspend fun withdrawMember(
        @RequestBody
        request: WithdrawMemberRequest,
        @RequestHeader(AUTHORIZATION)
        authorization: String,
    ): WithdrawMemberResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: WithdrawMemberCommand = request.toCommand()
        memberService.withdraw(command, requesterId)
        return WithdrawMemberResponse()
    }
}
