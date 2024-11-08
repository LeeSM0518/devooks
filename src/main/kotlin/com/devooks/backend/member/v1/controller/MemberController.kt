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

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService,
    private val memberInfoService: MemberInfoService,
    private val categoryService: CategoryService,
    private val favoriteCategoryService: FavoriteCategoryService,
    private val tokenService: TokenService,
): MemberControllerDocs {

    @Transactional
    @PostMapping("/signup")
    override suspend fun signUp(
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
    override suspend fun modifyAccountInfo(
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
    override suspend fun modifyProfileImage(
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
    @PatchMapping("/profile")
    override suspend fun modifyProfile(
        @RequestBody
        request: ModifyProfileRequest,
        @RequestHeader(AUTHORIZATION, required = true)
        authorization: String,
    ): ModifyProfileResponse {
        val requesterId: UUID = tokenService.getMemberId(Authorization(authorization))
        val command: ModifyProfileCommand = request.toCommand()
        val member: Member = memberService.updateNickname(command, requesterId)
        val memberInfo: MemberInfo = memberInfoService.updateProfile(command, requesterId)
        val categoryList = command.favoriteCategoryIdList?.let {
            val categories: List<Category> = categoryService.getAll(it)
            favoriteCategoryService.deleteByMemberId(requesterId)
            favoriteCategoryService.save(categories, requesterId)
        }.let {
            val categoryIds = favoriteCategoryService.findByMemberId(requesterId).map { it.categoryId }
            categoryService.getAll(categoryIds)
        }
        return ModifyProfileResponse(member, memberInfo, categoryList, requesterId)
    }

    @GetMapping("/{memberId}/profile")
    override suspend fun getProfile(
        @PathVariable(required = true)
        memberId: UUID,
        @RequestHeader(AUTHORIZATION, required = false, defaultValue = "")
        authorization: String,
    ): GetProfileResponse {
        val requesterId = authorization
            .takeIf { it.isNotEmpty() }
            ?.let { tokenService.getMemberId(Authorization(authorization)) }
        val member: Member = memberService.findById(memberId)
        val memberInfo: MemberInfo = memberInfoService.findById(memberId)
        val categoryIdList: List<UUID> = favoriteCategoryService.findByMemberId(memberId).map { it.categoryId }
        val categoryList = categoryService.getAll(categoryIdList)
        return GetProfileResponse(member, memberInfo, categoryList, requesterId)
    }

    @Transactional
    @PatchMapping("/withdrawal")
    override suspend fun withdrawMember(
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
