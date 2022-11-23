package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import com.AlMLand.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("v1/courses")
class CourseController(private val courseService: CourseService) {

    @DeleteMapping("{id}")
    fun deleteCourse(@PathVariable id: Int): ResponseEntity<Any> {
        return if (courseService.deleteCourse(id)) ResponseEntity.ok().build() else ResponseEntity.notFound().build()
    }

    @PutMapping("{id}")
    fun updateCourse(
        @PathVariable id: Int,
        @Valid @RequestBody courseDTO: CourseDTO
    ): ResponseEntity<CourseDTO> {
        val updatedCourseDTO = courseService.updateCourses(id, courseDTO)
        return if (courseDTO == updatedCourseDTO) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedCourseDTO)
        } else {
            ResponseEntity.status(HttpStatus.OK).location(URI("v1/courses/${updatedCourseDTO.id}"))
                .body(updatedCourseDTO)
        }
    }

    @PostMapping
    fun createCourse(
        @Validated @RequestBody courseDTO: CourseDTO,
        bindingResult: BindingResult
    ): ResponseEntity<CourseDTO> {
        if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(courseDTO)
        val savedCourseDTO = courseService.createCourse(courseDTO)
        savedCourseDTO.id ?: return ResponseEntity.status(HttpStatus.CONFLICT).body(courseDTO)
        return ResponseEntity.created(URI("v1/courses/${savedCourseDTO.id}")).body(savedCourseDTO)
    }

    @GetMapping("/categories/{category}")
    fun getCourseByCategoryLike(@PathVariable category: String): ResponseEntity<List<CourseDTO>> {
        val courseDTOs = courseService.findCourseByNameLike(category)
        return when (courseDTOs.size) {
            0 -> ResponseEntity.noContent().build()
            else -> ResponseEntity.ok(courseDTOs)
        }
    }

    @GetMapping("{id}")
    fun getCourse(@PathVariable id: Int): ResponseEntity<CourseDTO> {
        val courseDTO = courseService.findCourse(id)
        courseDTO ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(courseDTO)
    }

    @GetMapping
    fun getAllCourses(@RequestParam(required = false) name: String?): ResponseEntity<List<CourseDTO>> {
        val courseDTOs = courseService.findAllCourses(name)
        if (courseDTOs.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(listOf())
        return ResponseEntity.ok(courseDTOs)
    }

}
