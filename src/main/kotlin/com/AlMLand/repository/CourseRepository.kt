package com.AlMLand.repository

import com.AlMLand.entity.Course
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> {
    fun existsFirst1ByNameAndCategory(name: String, category: String): Boolean
}