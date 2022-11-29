package com.AlMLand.repository

import com.AlMLand.entity.Instructor
import org.springframework.data.repository.CrudRepository
import java.util.*

interface InstructorRepository : CrudRepository<Instructor, Int> {
    fun existsByInstructorId_FirstName(firstName: String): Boolean
    fun existsByInstructorId_FirstNameAndInstructorId_LastName(firstName: String, lastName: String): Boolean
    fun findByInstructorId_FirstNameAndInstructorId_LastName(firstName: String, lastName: String): Optional<Instructor>
}
