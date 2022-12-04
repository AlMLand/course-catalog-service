package com.AlMLand.controller

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.service.CourseCategoryService
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

private const val PATH = "/v1/categories"

@RestController
@RequestMapping(PATH)
class CourseCategoryController(private val service: CourseCategoryService) {

    @PostMapping
    fun createCourseCategory(@RequestBody dto: CourseCategoryDTO): ResponseEntity<CourseCategoryDTO> {
        val newDTO = service.createCourseCategory(dto)
        return newDTO.id?.let {
            ResponseEntity.created(URI("$PATH/${newDTO.id}")).body(newDTO)
        }
            ?: ResponseEntity.status(CONFLICT).body(newDTO)
    }

    @GetMapping
    fun getAllCourseCategories(@RequestParam(required = false) courseName: String?): ResponseEntity<List<CourseCategoryDTO>> {
        val courseCategoriesDTOs = service.findAllCourseCategories(courseName)
        return if (courseCategoriesDTOs.isEmpty()) ResponseEntity.status(NOT_FOUND).body(listOf())
        else ResponseEntity.ok(courseCategoriesDTOs)
    }

    @GetMapping("/uuid")
    fun getIdByCategoryAndDescription(
        @RequestParam category: String,
        @RequestParam description: String?
    ): ResponseEntity<UUID> {
        val uuid = service.findIdByCategoryAndDescription(category, description)
        return uuid?.let {
            ResponseEntity.ok(uuid)
        } ?: ResponseEntity.status(NOT_FOUND).body(null)
    }

}
