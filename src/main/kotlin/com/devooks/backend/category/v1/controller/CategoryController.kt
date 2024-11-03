package com.devooks.backend.category.v1.controller

import com.devooks.backend.category.v1.dto.GetCategoriesResponse
import com.devooks.backend.category.v1.dto.GetCategoriesResponse.Companion.toResponse
import com.devooks.backend.category.v1.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryService: CategoryService,
): CategoryControllerDocs {

    @GetMapping
    override suspend fun getCategories(): GetCategoriesResponse {
        return categoryService.getAll().toResponse()
    }

}
