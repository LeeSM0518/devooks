package com.devooks.backend.category.v1.controller

import com.devooks.backend.category.v1.dto.GetCategoriesRequest
import com.devooks.backend.category.v1.dto.GetCategoriesResponse
import com.devooks.backend.category.v1.dto.GetCategoriesResponse.Companion.toResponse
import com.devooks.backend.category.v1.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryService: CategoryService,
) {

    @GetMapping
    suspend fun getCategories(
        @RequestParam(required = false, defaultValue = "")
        name: String,
        @RequestParam(required = false, defaultValue = "")
        page: String,
        @RequestParam(required = false, defaultValue = "")
        count: String,
    ): GetCategoriesResponse {
        val request = GetCategoriesRequest(name, page, count)
        return categoryService.get(request).toResponse()
    }

}