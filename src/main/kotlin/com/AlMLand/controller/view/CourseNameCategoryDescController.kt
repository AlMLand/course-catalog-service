package com.AlMLand.controller.view

import com.AlMLand.entity.view.CourseNameCategoryDesc
import com.AlMLand.service.CourseNameCategoryDescService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val PAGEABLE_DEFAULT = Pageable.ofSize(2)
private const val PATH = "v1/namesdescriptions"

@RestController
@RequestMapping(PATH)
class CourseNameCategoryDescController(private val service: CourseNameCategoryDescService) {
    @GetMapping("all")
    fun findAll() = service.findAll().let {
        if (it.isNotEmpty()) ResponseEntity.ok(it)
        else ResponseEntity.notFound()
    }

    @GetMapping
    fun findByDescriptionLike(
        @RequestParam searchText: String,
        @RequestParam(required = false) pageable: Pageable?
    ): ResponseEntity<Page<CourseNameCategoryDesc>> =
        service.findByDescriptionLike(searchText, pageable ?: PAGEABLE_DEFAULT).let {
            if (!it.isEmpty) ResponseEntity.ok(it)
            else ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty())
        }

}