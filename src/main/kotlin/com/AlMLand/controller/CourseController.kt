package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import com.AlMLand.dto.enums.Category
import com.AlMLand.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*
import javax.validation.Valid

private const val PATH = "v1/courses"

@Validated
@RestController
@RequestMapping(PATH)
class CourseController(private val service: CourseService) {

    @DeleteMapping("{id}")
    fun deleteCourse(@PathVariable id: UUID): ResponseEntity<Any> =
        if (service.deleteCourse(id)) ResponseEntity.ok().build() else ResponseEntity.notFound().build()

    @PutMapping("{id}")
    fun updateCourse(
        @PathVariable id: UUID,
        @Valid @RequestBody dto: CourseDTO
    ): ResponseEntity<CourseDTO> {
        val updatedDTO = service.updateCourses(id, dto)
        return if (dto == updatedDTO) ResponseEntity.status(NOT_FOUND).body(updatedDTO)
        else ResponseEntity.status(HttpStatus.OK).location(URI("$PATH/${updatedDTO.id}"))
            .body(updatedDTO)
    }

    @PostMapping
    fun createCourse(
        @Validated @RequestBody dto: CourseDTO,
        bindingResult: BindingResult
    ): ResponseEntity<CourseDTO> {
        return if (bindingResult.hasErrors()) ResponseEntity.badRequest().body(dto)
        else {
            val savedDTO = service.createCourse(dto)
            savedDTO.id ?: return ResponseEntity.status(CONFLICT).body(dto)
            ResponseEntity.created(URI("$PATH/${savedDTO.id}")).body(savedDTO)
        }
    }

    @GetMapping("{id}")
    fun getCourse(@PathVariable id: UUID): ResponseEntity<CourseDTO> {
        val dto = service.findCourse(id)
        return dto?.let { ResponseEntity.ok(dto) } ?: ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllCourses(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) category: Category?
    ): ResponseEntity<List<CourseDTO>> {
        val courseDTOs = service.findAllCourses(name, category)
        return if (courseDTOs.isEmpty()) ResponseEntity.status(NOT_FOUND).body(listOf())
        else ResponseEntity.ok(courseDTOs)
    }

}
