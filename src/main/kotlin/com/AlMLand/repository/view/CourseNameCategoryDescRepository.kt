package com.AlMLand.repository.view;

import com.AlMLand.entity.view.CourseNameCategoryDesc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CourseNameCategoryDescRepository : JpaRepository<CourseNameCategoryDesc, UUID> {
    fun findByDescriptionLike(description: String, pageable: Pageable): Page<CourseNameCategoryDesc>
}