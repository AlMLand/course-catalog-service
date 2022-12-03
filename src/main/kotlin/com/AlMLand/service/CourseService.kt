package com.AlMLand.service

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.CourseDTO
import com.AlMLand.dto.InstructorIdDTO
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
import java.util.*

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val instructorRepository: InstructorRepository,
    private val courseCategoryRepository: CourseCategoryRepository
) {

    private companion object : KLogging()

    fun createCourse(dto: CourseDTO): CourseDTO {
        return if (existsAlready(dto)) {
            dto
        } else {
            val instructor = getInstructorWhenValid(dto)
            categoryValidation(dto)

            val savedCourse = courseRepository.save(dto.let {
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
                    it.categories.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                    it.id,
                    it.instructor.instructorId.let { iid -> InstructorIdDTO(iid.firstName, iid.lastName) }
                )
            }
        }
    }

    private fun categoryValidation(dto: CourseDTO) = dto.category.forEach {
        if (it.category == null) throw CategoryNotValidException("Category must not be null: $dto")
        if (!courseCategoryRepository.existsById(it.id!!))
            throw CategoryNotValidException("Category with id: ${it.id}, not exists")
    }

    private fun getInstructorWhenValid(dto: CourseDTO): Instructor =
        instructorRepository.findByInstructorId_FirstNameAndInstructorId_LastName(
            dto.instructorId.firstName,
            dto.instructorId.lastName
        )
            .orElseThrow { InstructorNotValidException("This instructor not exists, instructor id: ${dto.instructorId}") }

    private fun existsAlready(dto: CourseDTO) =
        courseRepository.existsFirst1ByNameAndCategoriesIn(
            dto.name,
            dto.category.map { CourseCategory(it.category, it.id, it.description) })

    fun findCourse(id: UUID): CourseDTO? {
        val courseDTO = courseRepository.findByIdOrNull(id)?.let {
            CourseDTO(
                it.name,
                it.categories.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                it.id,
                it.instructor.instructorId.let { iid -> InstructorIdDTO(iid.firstName, iid.lastName) }
            )
        }
        logger.info { "founded course as courseDTO: $courseDTO" }
        return courseDTO
    }


    fun findAllCourses(name: String?, category: Category?): List<CourseDTO> {
        val courses = if (name != null && category != null) {
            courseRepository.findByNameContainingIgnoreCaseAndCategoriesCategory(name, category)
        } else {
            name?.let {
                courseRepository.findByNameContainingIgnoreCase(name)
            } ?: category?.let {
                courseRepository.findByCategoriesCategory(category)
            } ?: courseRepository.findAll()
        }

        return courses.map {
            CourseDTO(
                it.name,
                it.categories.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                it.id,
                it.instructor.instructorId.let { iid -> InstructorIdDTO(iid.firstName, iid.lastName) }
            )
        }
    }

    fun updateCourses(id: UUID, dto: CourseDTO): CourseDTO {
        val courseInDB = courseRepository.findById(id)
        return if (courseInDB.isPresent) {
            courseInDB.get().let {
                it.name = dto.name
                it.categories =
                    dto.category.map { cc -> CourseCategory(cc.category, cc.id, cc.description) }.toMutableList()
                it.instructor = getInstructorWhenValid(dto)
                courseRepository.save(it)
                CourseDTO(
                    it.name,
                    it.categories.map { cc -> CourseCategoryDTO(cc.category, cc.id, cc.description) }.toMutableList(),
                    it.id,
                    it.instructor.instructorId.let { iid -> InstructorIdDTO(iid.firstName, iid.lastName) }
                )
            }
        } else {
            dto
        }
    }

    fun deleteCourse(id: UUID): Boolean {
        return if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id)
            true
        } else
            false
    }

}
