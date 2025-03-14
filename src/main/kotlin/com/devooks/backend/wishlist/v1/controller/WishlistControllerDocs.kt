package com.devooks.backend.wishlist.v1.controller

import com.devooks.backend.common.dto.PageResponse
import com.devooks.backend.common.exception.ErrorResponse
import com.devooks.backend.ebook.v1.dto.EbookView
import com.devooks.backend.wishlist.v1.dto.CreateWishlistRequest
import com.devooks.backend.wishlist.v1.dto.CreateWishlistResponse
import com.devooks.backend.wishlist.v1.dto.DeleteWishlistResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Tag(name = "Wishlist", description = "찜")
interface WishlistControllerDocs {

    @Operation(summary = "찜 등록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateWishlistResponse::class)
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
                "- EBOOK-404-1 : 전자책을 찾을 수 없습니다",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
            ApiResponse(
                responseCode = "409",
                description = "- WISHLIST-409-1 : 이미 존재하는 찜입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            )
        ]
    )
    suspend fun createWishlist(
        request: CreateWishlistRequest,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): CreateWishlistResponse

    @Operation(summary = "찜 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
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
        ]
    )
    suspend fun getWishlist(
        @Schema(description = "카테고리 식별자 목록", format = "uuid", required = false)
        categoryIdList: List<UUID>?,
        @Schema(description = "페이지", implementation = Int::class, required = true)
        page: Int,
        @Schema(description = "개수", implementation = Int::class, required = true)
        count: Int,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): PageResponse<EbookView>

    @Operation(summary = "찜 취소")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DeleteWishlistResponse::class)
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
                "- WISHLIST-403-1 : 자신의 찜만 삭제할 수 있습니다.",
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
                "- WISHLIST-404-1 : 존재하지 않는 찜입니다.",
                content = arrayOf(
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class)
                    )
                )
            ),
        ]
    )
    suspend fun deleteWishlist(
        @Schema(description = "찜 식별자", required = true, implementation = UUID::class)
        wishlistId: UUID,
        @Schema(description = "액세스 토큰", example = "Bearer \${accessToken}", required = true, hidden = true)
        authorization: String,
    ): DeleteWishlistResponse
}
