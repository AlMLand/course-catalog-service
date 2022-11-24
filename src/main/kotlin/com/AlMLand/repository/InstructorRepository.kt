package com.AlMLand.repository

import com.AlMLand.entity.Instructor
import org.springframework.data.repository.CrudRepository

interface InstructorRepository : CrudRepository<Instructor, Int> {
    fun existsByName(name: String): Boolean
}