package com.AlMLand.service

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.entity.CourseCategory
import com.AlMLand.repository.CourseCategoryRepository
import org.springframework.stereotype.Service

@Service
class CourseCategoryService(private val repository: CourseCategoryRepository) {
    fun createCourseCategory(courseCategoryDTO: CourseCategoryDTO): CourseCategoryDTO {
        if (courseCategoryExists(courseCategoryDTO)) return courseCategoryDTO
        return repository.save(courseCategoryDTO.let {
            CourseCategory(
                it.category,
                null,
                it.description
            )
        }).let { CourseCategoryDTO(it.category, it.id, it.description) }
    }

    private fun courseCategoryExists(dto: CourseCategoryDTO): Boolean {
        return repository.existsByCategoryAndDescription(dto.category, dto.description)
    }

    fun findAllCourseCategories(courseName: String?): List<CourseCategoryDTO> {
        val courseCategories = courseName?.let { repository.findByCoursesName(courseName) }
            ?: repository.findAll()
        return courseCategories.map {
            CourseCategoryDTO(it.category, it.id, it.description)
        }
    }
}
