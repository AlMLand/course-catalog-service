package com.AlMLand.service

import com.AlMLand.dto.CourseCategoryDTO
import com.AlMLand.dto.enums.Category
import com.AlMLand.entity.CourseCategory
import com.AlMLand.exception.customexceptions.CategoryNotExistsException
import com.AlMLand.repository.CourseCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CourseCategoryService(private val repository: CourseCategoryRepository) {
    @Transactional
    fun createCourseCategory(dto: CourseCategoryDTO): CourseCategoryDTO {
        if (courseCategoryExists(dto)) return dto
        return repository.save(dto.let {
            CourseCategory(
                it.category,
                null,
                it.description
            )
        }).let { CourseCategoryDTO(it.category, it.id, it.description) }
    }

    private fun courseCategoryExists(dto: CourseCategoryDTO) =
        repository.existsByCategoryAndDescription(dto.category, dto.description)

    @Transactional(readOnly = true)
    fun findAllCourseCategories(courseName: String?): List<CourseCategoryDTO> {
        val courseCategories = courseName?.let { repository.findByCoursesName(courseName) }
            ?: repository.findAll()
        return courseCategories.map {
            CourseCategoryDTO(it.category, it.id, it.description)
        }
    }

    @Transactional(readOnly = true)
    fun findIdByCategoryAndDescription(category: String, description: String?): UUID? {
        val categories = Category.values()
        return if (categories.map { it.name }.contains(category)) repository.findIdByCategoryAndDescription(
            categories.find { it.name == category }!!,
            description
        )
        else throw CategoryNotExistsException("The category $category does not exists")
    }
}
