package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.service.CourseCategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("v1/categories")
class CourseCategoryController(private val service: CourseCategoryService) {

    @PostMapping
    fun createCourseCategory(@RequestBody courseCategoryDTO: CourseCategoryDTO): ResponseEntity<CourseCategoryDTO> {
        val newCourseCategory = service.createCourseCategory(courseCategoryDTO)
        newCourseCategory.id ?: return ResponseEntity.status(HttpStatus.CONFLICT).body(newCourseCategory)
        return ResponseEntity.created(URI("/v1/categories/${newCourseCategory.id}")).body(newCourseCategory)
    }

    @GetMapping
    fun getAllCourseCategories(@RequestParam(required = false) courseName: String?): ResponseEntity<List<CourseCategoryDTO>> {
        val courseCategories = service.findAllCourseCategories(courseName)
        if (courseCategories.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(listOf())
        return ResponseEntity.ok(courseCategories)
    }

}
