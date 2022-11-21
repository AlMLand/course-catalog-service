package com.AlMLand.controller

import com.AlMLand.dto.CourseDTO
import com.AlMLand.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("v1/courses")
class CourseController(private val courseService: CourseService) {

    @DeleteMapping("{id}")
    fun deleteCourse(@PathVariable id: Int): ResponseEntity<Any> {
        return if (courseService.deleteCourse(id)) ResponseEntity.ok().build() else ResponseEntity.notFound().build()
    }

    @PutMapping("{id}")
    fun updateCourse(@PathVariable id: Int, @RequestBody courseDTO: CourseDTO): ResponseEntity<CourseDTO> {
        val updatedCourseDTO = courseService.updateCourses(id, courseDTO)
        return if (courseDTO == updatedCourseDTO) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedCourseDTO)
        } else {
            ResponseEntity.status(HttpStatus.OK).location(URI("v1/courses/${updatedCourseDTO.id}"))
                .body(updatedCourseDTO)
        }
    }

    @PostMapping
    fun createCourse(@RequestBody courseDTO: CourseDTO): ResponseEntity<CourseDTO> {
        val savedCourseDTO = courseService.createCourse(courseDTO)
        savedCourseDTO.id ?: return ResponseEntity.status(HttpStatus.CONFLICT).body(courseDTO)
        return ResponseEntity.created(URI("v1/courses/${savedCourseDTO.id}")).body(savedCourseDTO)
    }

    @GetMapping("{id}")
    fun findCourse(@PathVariable id: Int): ResponseEntity<CourseDTO> {
        val courseDTO = courseService.findCourse(id)
        courseDTO ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(courseDTO)
    }

    @GetMapping
    fun findAllCourses(): ResponseEntity<List<CourseDTO>> {
        val courseDTOs = courseService.findAllCourses()
        if (courseDTOs.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(listOf())
        return ResponseEntity.ok(courseDTOs)
    }

}
