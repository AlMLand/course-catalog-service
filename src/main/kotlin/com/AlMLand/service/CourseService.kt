package com.AlMLand.service

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.CourseDTO
import com.AlMLand.dto.enums.Category
import com.AlMLand.entity.Course
import com.AlMLand.entity.CourseCategory
import com.AlMLand.entity.Instructor
import com.AlMLand.exception.CategoryNotValidException
import com.AlMLand.exception.InstructorNotValidException
import com.AlMLand.repository.CourseCategoryRepository
import com.AlMLand.repository.CourseRepository
import com.AlMLand.repository.InstructorRepository
import mu.KLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val instructorRepository: InstructorRepository,
    private val courseCategoryRepository: CourseCategoryRepository
) {

    private companion object : KLogging()

    fun createCourse(courseDTO: CourseDTO): CourseDTO {
        return if (existsAlready(courseDTO)) {
            courseDTO
        } else {
            val instructor = getInstructorWhenValide(courseDTO)
            categoryValidation(courseDTO)

            val savedCourse = courseRepository.save(courseDTO.let {
                Course(
                    it.name,
                    it.category.map { cc -> CourseCategory(cc.category, cc.id, cc.description) }.toMutableList(),
                    null,
                    instructor
                )
            })
            logger.info { "saved course: $savedCourse" }
            savedCourse.let {
                CourseDTO(
                    it.name,
                    it.category.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                    it.id,
                    it.instructor.id!!
                )
            }
        }
    }

    private fun categoryValidation(courseDTO: CourseDTO) = courseDTO.category.forEach {
        if (it.category == null) throw CategoryNotValidException("Category must not be null: $courseDTO")
        if (!courseCategoryRepository.existsById(it.id!!))
            throw CategoryNotValidException("Category with id: ${it.id}, not exists")
    }

    private fun getInstructorWhenValide(courseDTO: CourseDTO): Instructor =
        instructorRepository.findById(courseDTO.instructorId)
            .orElseThrow { InstructorNotValidException("This instructor not exists, instructor id: ${courseDTO.instructorId}") }

    private fun existsAlready(courseDTO: CourseDTO) =
        courseRepository.existsFirst1ByNameAndCategoryIn(
            courseDTO.name,
            courseDTO.category.map { CourseCategory(it.category, it.id, it.description) })

    fun findCourse(id: Int): CourseDTO? {
        val courseDTO = courseRepository.findByIdOrNull(id)?.let {
            CourseDTO(
                it.name,
                it.category.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                it.id,
                it.instructor.id!!
            )
        }
        logger.info { "founded course as courseDTO: $courseDTO" }
        return courseDTO
    }


    fun findAllCourses(name: String?, category: Category?): List<CourseDTO> {
        val courses = if (name != null && category != null) {
            courseRepository.findByNameContainingIgnoreCaseAndCategoryCategory(name, category)
        } else {
            name?.let {
                courseRepository.findByNameContainingIgnoreCase(name)
            } ?: category?.let {
                courseRepository.findByCategoryCategory(category)
            } ?: courseRepository.findAll()
        }

        return courses.map {
            CourseDTO(
                it.name,
                it.category.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                it.id,
                it.instructor.id!!
            )
        }
    }

    fun updateCourses(id: Int, courseDTO: CourseDTO): CourseDTO {
        val courseInDB = courseRepository.findById(id)
        return if (courseInDB.isPresent) {
            courseInDB.get().let {
                it.name = courseDTO.name
                it.category =
                    courseDTO.category.map { cc -> CourseCategory(cc.category, cc.id, cc.description) }.toMutableList()
                courseRepository.save(it)
                CourseDTO(
                    it.name,
                    it.category.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                    it.id,
                    it.instructor.id!!
                )
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

}
