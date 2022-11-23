package com.AlMLand.service

import com.AlMLand.dto.CourseDTO
import com.AlMLand.entity.Course
import com.AlMLand.exception.InstructorNotValidException
import com.AlMLand.repository.CourseRepository
import com.AlMLand.repository.InstructorRepository
import mu.KLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val instructorRepository: InstructorRepository
) {

    private companion object : KLogging()

    fun createCourse(courseDTO: CourseDTO): CourseDTO {
        return if (existsAlready(courseDTO)) {
            courseDTO
        } else {
            val instructor = instructorRepository.findById(courseDTO.instructorId)
                .orElseThrow { InstructorNotValidException("This instructor not exists, instructor id: ${courseDTO.instructorId}") }

            val savedCourse = courseRepository.save(courseDTO.let {
                Course(it.name, it.category, null, instructor)
            })
            logger.info { "saved course: $savedCourse" }
            savedCourse.let {
                CourseDTO(it.name, it.category, it.id, it.instructor.id!!)
            }
        }
    }

    private fun existsAlready(courseDTO: CourseDTO) =
        courseRepository.existsFirst1ByNameAndCategory(courseDTO.name, courseDTO.category)

    fun findCourse(id: Int): CourseDTO? {
        val courseDTO = courseRepository.findByIdOrNull(id)?.let {
            CourseDTO(it.name, it.category, it.id, it.instructor.id!!)
        }
        logger.info { "founded course as courseDTO: $courseDTO" }
        return courseDTO
    }


    fun findAllCourses(name: String?): List<CourseDTO> {
        val courses = name?.let {
            courseRepository.findByNameContainingIgnoreCase(name)
        } ?: courseRepository.findAll()

        return courses.map { CourseDTO(it.name, it.category, it.id, it.instructor.id!!) }
    }

    fun updateCourses(id: Int, courseDTO: CourseDTO): CourseDTO {
        val courseInDB = courseRepository.findById(id)
        return if (courseInDB.isPresent) {
            courseInDB.get().let {
                it.name = courseDTO.name
                it.category = courseDTO.category
                courseRepository.save(it)
                CourseDTO(it.name, it.category, it.id, it.instructor.id!!)
            }
        } else {
            courseDTO
        }
    }

    fun deleteCourse(id: Int): Boolean {
        return if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id)
            true
        } else
            false
    }

    fun findCourseByCategoryLike(category: String): List<CourseDTO> =
        courseRepository.findByCategoryContainingIgnoreCase(category)
            .map { CourseDTO(it.name, it.category, it.id, it.instructor.id!!) }

}
