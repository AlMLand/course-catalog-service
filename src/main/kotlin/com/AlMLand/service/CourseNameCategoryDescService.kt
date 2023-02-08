package com.AlMLand.service

import com.AlMLand.repository.view.CourseNameCategoryDescRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CourseNameCategoryDescService(private val repository: CourseNameCategoryDescRepository) {
    fun findAll() = repository.findAll()
    fun findByDescriptionLike(searchText: String, pageable: Pageable) =
        repository.findByDescriptionLike(searchText, pageable)
}